package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.StringResource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhoneNumberViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val context: Context = mockk()
    private val appScreenEvents: AppScreenEvents = mockk(relaxUnitFun = true)
    private val phoneNumberService: PhoneNumberService = mockk()
    private val messageNotifier: MessageNotifier = mockk(relaxUnitFun = true)
    private val phoneNumber = "6502341234"
    private val json =
        """
        {
          "codes": [
            { "name": "United States", "iso": "US", "dial_code": "+1", "emoji": "🇺🇸" }
          ]
        }
        """.trimIndent()

    private val viewModel by lazy {
        PhoneNumberViewModel(
            context = context,
            appScreenEvents = appScreenEvents,
            phoneNumberService = phoneNumberService,
            messageNotifier = messageNotifier,
        )
    }

    @Before
    fun setUp() {
        every { context.getString(any()) } returns ""

        val resources: Resources = mockk()
        every { context.resources } returns resources
        every { resources.openRawResource(R.raw.country_codes) } returns json.byteInputStream()
    }

    @Test
    fun `it should have correct initial state`() {
        // when
        val buttonTitle = "Send Verification Message"
        every { context.getString(R.string.send_verification_message_button_title) } returns buttonTitle
        val initialState = viewModel.uiState.value

        // then
        assertThat(initialState.title).isEqualTo(StringResource(R.string.account_settings_add_phone_number))
        assertThat(initialState.step).isInstanceOf(PhoneNumberInputUiModel::class.java)
        assertThat(initialState.actionButton.title).isEqualTo(buttonTitle)
        assertThat(initialState.actionButton.enabled).isFalse()
    }

    @Test
    fun `it should update state when phone number is entered`() {
        // given
        val inputStep = viewModel.uiState.value.step as PhoneNumberInputUiModel

        // when
        inputStep.onPhoneNumberChanged(phoneNumber)

        // then
        val updatedState = viewModel.uiState.value
        assertThat(updatedState.step).isInstanceOf(PhoneNumberInputUiModel::class.java)
        assertThat((updatedState.step as PhoneNumberInputUiModel).phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `it should handle start phone number verification success correctly`() = runTest {
        // given
        val uiState = viewModel.uiState.value
        val inputStep = uiState.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        coEvery {
            phoneNumberService.startPhoneNumberVerification("+1$phoneNumber")
        } returns Result.success(Unit)

        // when
        uiState.actionButton.action()

        // then
        val updatedState = viewModel.uiState.value
        assertThat(updatedState.step).isInstanceOf(VerificationCodeUiModel::class.java)
    }

    @Test
    fun `it should handle start phone number verification failure correctly`() = runTest {
        // given
        val uiState = viewModel.uiState.value
        val inputStep = uiState.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        coEvery {
            phoneNumberService.startPhoneNumberVerification("+1$phoneNumber")
        } returns Result.failure(Exception("Error"))

        // when
        uiState.actionButton.action()

        // then
        val newState = viewModel.uiState.value
        assertThat(newState.step).isInstanceOf(PhoneNumberInputUiModel::class.java)
        assertThat((newState.step as PhoneNumberInputUiModel).phoneNumber).isEqualTo(phoneNumber)
        verify { messageNotifier.notify(R.string.error_sending_verification_message_message) }
    }

    @Test
    fun `it should handle start checkPhoneNumberVerification success correctly`() = runTest {
        // given
        coEvery {
            phoneNumberService.startPhoneNumberVerification("+1$phoneNumber")
        } returns Result.success(Unit)
        val uiState = viewModel.uiState.value
        val inputStep = uiState.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        uiState.actionButton.action()
        val newUiState = viewModel.uiState.value.step as VerificationCodeUiModel
        repeat(6) { newUiState.onValueChanged(it, "1") }
        coEvery {
            phoneNumberService.checkPhoneNumberVerification(any(), any())
        } returns Result.success(Unit)

        // when
        viewModel.uiState.value.actionButton.action()

        // then
        verify { messageNotifier.notify(R.string.phone_number_verification_success_message) }
        verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `it should show country code selection bottom sheet`() = runTest {
        // when
        (viewModel.uiState.value.step as PhoneNumberInputUiModel).onCountryCodeButtonClicked()

        // then
        val state = viewModel.uiState.value
        val step = state.step as PhoneNumberInputUiModel
        val countrySelection = step.countrySelection
        assertThat(countrySelection).isNotNull()
        assertThat(countrySelection?.items).hasSize(1)
        assertThat(countrySelection?.items?.first()?.countryName).isEqualTo("United States")
    }

    @Test
    fun `it should handle search query and item filtering correctly`() = runTest {
        // given
        (viewModel.uiState.value.step as PhoneNumberInputUiModel).onCountryCodeButtonClicked()
        val state = viewModel.uiState.value
        val step = state.step as PhoneNumberInputUiModel
        val countrySelection = step.countrySelection

        // when
        countrySelection?.onSearchQueryChanged?.invoke("asdfd")

        // then
        val newSelection = (viewModel.uiState.value.step as PhoneNumberInputUiModel).countrySelection
        assertThat(newSelection).isNotNull()
        assertThat(newSelection?.items).isEmpty()
    }
}
