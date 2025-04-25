package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.account.manager.UserState
import edu.stanford.spezi.modules.navigation.NavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.testing.verifyNever
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.StringResource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhoneNumberSettingsViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val context: Context = mockk()
    private val phoneNumberService: PhoneNumberService = mockk()
    private val messageNotifier: MessageNotifier = mockk(relaxUnitFun = true)
    private val userSessionManager: UserSessionManager = mockk()
    private val navigator: Navigator = mockk(relaxUnitFun = true)
    private val phoneNumber = "6502341234"

    private val viewModel by lazy {
        PhoneNumberSettingsViewModel(
            context = context,
            phoneNumberService = phoneNumberService,
            messageNotifier = messageNotifier,
            navigator = navigator,
            userSessionManager = userSessionManager,
        )
    }

    @Before
    fun setUp() {
        every { context.getString(any()) } returns ""
        coEvery { phoneNumberService.getAllCountryCodes() } returns listOf(
            CountryCode(
                name = "United States",
                iso = "US",
                dialCode = "+1",
                emoji = ""
            )
        )
        every { phoneNumberService.format(phoneNumber) } returns phoneNumber
        every { phoneNumberService.isPhoneNumberValid(any(), any()) } returns true
        every { userSessionManager.observeRegisteredUser() } returns flowOf(
            UserState.Registered(
                hasInvitationCodeConfirmed = true,
                disabled = true,
                phoneNumbers = listOf(phoneNumber)
            )
        )
    }

    @Test
    fun `it should have correct initial state`() {
        assertThat(viewModel.uiState.value.phoneNumbers.first().phoneNumber).isEqualTo(phoneNumber)
        assertThat(viewModel.uiState.value.bottomSheet).isNull()
    }

    @Test
    fun `it should handle phone number deletion correctly`() = runTest {
        // given
        coEvery { phoneNumberService.deletePhoneNumber(phoneNumber) } returns Result.success(Unit)
        val phoneNumber = viewModel.uiState.value.phoneNumbers.first()

        // when
        phoneNumber.onDeleteClicked()

        // then
        verifyNever { messageNotifier.notify(R.string.phone_number_deletion_error_message) }
    }

    @Test
    fun `it should handle phone number deletion failure correctly`() = runTest {
        // given
        coEvery { phoneNumberService.deletePhoneNumber(phoneNumber) } returns Result.failure(Exception("Error"))
        val phoneNumber = viewModel.uiState.value.phoneNumbers.first()

        // when
        phoneNumber.onDeleteClicked()

        // then
        verify { messageNotifier.notify(R.string.phone_number_deletion_error_message) }
    }

    @Test
    fun `it should handle on back clicked correctly`() {
        // given
        val uiState = viewModel.uiState.value

        // when
        uiState.onBackClicked()

        // then
        verify { navigator.navigateTo(NavigationEvent.PopBackStack) }
    }

    @Test
    fun `it should have correct initial bottom sheet state`() {
        // when
        val buttonTitle = "Send Verification Message"
        every { context.getString(R.string.send_verification_message_button_title) } returns buttonTitle
        setupBottomSheet()
        val initialState = requireBottomSheet()

        // then
        assertThat(initialState.title).isEqualTo(StringResource(R.string.phone_number_add))
        assertThat(initialState.step).isInstanceOf(PhoneNumberInputUiModel::class.java)
        assertThat(initialState.actionButton.title).isEqualTo(buttonTitle)
        assertThat(initialState.actionButton.enabled).isFalse()
    }

    @Test
    fun `it should update state when phone number is entered`() {
        // given
        setupBottomSheet()
        val inputStep = requireBottomSheet().step as PhoneNumberInputUiModel

        // when
        inputStep.onPhoneNumberChanged(phoneNumber)

        // then
        val updatedState = requireBottomSheet()
        assertThat(updatedState.step).isInstanceOf(PhoneNumberInputUiModel::class.java)
        assertThat((updatedState.step as PhoneNumberInputUiModel).phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    fun `it should handle start phone number verification success correctly`() = runTest {
        // given
        setupBottomSheet()
        val bottomSheet = requireBottomSheet()
        val inputStep = bottomSheet.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        coEvery {
            phoneNumberService.startPhoneNumberVerification("+1$phoneNumber")
        } returns Result.success(Unit)

        // when
        bottomSheet.actionButton.action()

        // then
        val updatedState = requireBottomSheet()
        assertThat(updatedState.step).isInstanceOf(VerificationCodeUiModel::class.java)
    }

    @Test
    fun `it should handle start phone number verification failure correctly`() = runTest {
        // given
        setupBottomSheet()
        val bottomSheet = requireBottomSheet()
        val inputStep = bottomSheet.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        coEvery {
            phoneNumberService.startPhoneNumberVerification("+1$phoneNumber")
        } returns Result.failure(Exception("Error"))

        // when
        bottomSheet.actionButton.action()

        // then
        val newState = requireBottomSheet()
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
        setupBottomSheet()
        val bottomSheet = requireBottomSheet()
        val inputStep = bottomSheet.step as PhoneNumberInputUiModel
        inputStep.onPhoneNumberChanged(phoneNumber)
        bottomSheet.actionButton.action()
        val newUiState = requireBottomSheet().step as VerificationCodeUiModel
        repeat(6) { newUiState.onValueChanged(it, "1") }
        coEvery {
            phoneNumberService.checkPhoneNumberVerification(any(), any())
        } returns Result.success(Unit)

        // when
        requireBottomSheet().actionButton.action()

        // then
        verify { messageNotifier.notify(R.string.phone_number_verification_success_message) }
        assertThat(viewModel.uiState.value.bottomSheet).isNull()
    }

    @Test
    fun `it should show country code selection bottom sheet`() = runTest {
        // when
        setupBottomSheet()
        (requireBottomSheet().step as PhoneNumberInputUiModel).onCountryCodeButtonClicked()

        // then
        val state = requireBottomSheet()
        val step = state.step as PhoneNumberInputUiModel
        val countrySelection = step.countrySelection
        assertThat(countrySelection).isNotNull()
        assertThat(countrySelection?.items).hasSize(1)
        assertThat(countrySelection?.items?.first()?.countryName).isEqualTo("United States")
    }

    @Test
    fun `it should handle search query and item filtering correctly`() = runTest {
        // given
        setupBottomSheet()
        (requireBottomSheet().step as PhoneNumberInputUiModel).onCountryCodeButtonClicked()
        val state = requireBottomSheet()
        val step = state.step as PhoneNumberInputUiModel
        val countrySelection = step.countrySelection

        // when
        countrySelection?.onSearchQueryChanged?.invoke("asdfd")

        // then
        val newSelection = (requireBottomSheet().step as PhoneNumberInputUiModel).countrySelection
        assertThat(newSelection).isNotNull()
        assertThat(newSelection?.items).isEmpty()
    }

    private fun setupBottomSheet() {
        viewModel.uiState.value.onAddPhoneNumberClicked()
    }

    private fun requireBottomSheet() = requireNotNull(viewModel.uiState.value.bottomSheet)
}
