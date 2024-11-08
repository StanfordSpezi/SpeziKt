package edu.stanford.spezi.module.account.register

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.coVerifyNever
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.account.AccountEvents
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var registerViewModel: RegisterViewModel
    private val authenticationManager: AuthenticationManager = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: AuthValidator = mockk(relaxed = true)

    @Before
    fun setUp() {
        with(validator) {
            every { isFormValid(any(), any()) } returns true
            every { isValidEmail(any()) } returns AuthValidator.Result.Valid
            every { isValidPassword(any()) } returns AuthValidator.Result.Valid
        }
        registerViewModel = RegisterViewModel(
            authenticationManager = authenticationManager,
            messageNotifier = messageNotifier,
            accountEvents = accountEvents,
            authValidator = validator
        )
    }

    @Test
    fun `it should update email correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("email", TextFieldType.EMAIL)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.email.value).isEqualTo("email")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should update password correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("password", TextFieldType.PASSWORD)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.password.value).isEqualTo("password")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should enable registered button when all fields available`() {
        // given
        val neededActions = buildList {
            TextFieldType.entries.forEach {
                add(Action.TextFieldUpdate("some-value", it))
            }
        }

        // when
        neededActions.forEach {
            registerViewModel.onAction(it)
        }

        // then
        assertThat(registerViewModel.uiState.value.isRegisterButtonEnabled).isTrue()
    }

    @Test
    fun `it should handle on Register for google sign up with email failure correctly`() =
        runTestUnconfined {
            // given
            val displayName = "Display Name"
            val idToken = "idToken"
            val token: GoogleIdTokenCredential = mockk {
                every { this@mockk.displayName } returns displayName
                every { this@mockk.idToken } returns idToken
            }
            coEvery { authenticationManager.getCredential(any()) } returns token
            val uiState = registerViewModel.uiState.value
            coEvery {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                )
            } returns Result.failure(Exception("Error"))

            // when
            registerViewModel.onAction(Action.OnRegisterPressed)

            // then
            coVerify {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                )
            }
            verify { accountEvents.emit(AccountEvents.Event.SignUpFailure) }
            verify { messageNotifier.notify("Failed to sign up") }
        }

    @Test
    fun `it should reevaluate in case form is not valid for google sign up with email failure correctly`() =
        runTestUnconfined {
            // given
            every { validator.isFormValid(any(), any()) } returns false

            // when
            registerViewModel.onAction(Action.OnRegisterPressed)

            // then
            coVerifyNever {
                authenticationManager.signUpWithEmailAndPassword(
                    email = any(),
                    password = any(),
                )
            }
            val uiState = registerViewModel.uiState.value
            with(validator) {
                verify { isFormValid(uiState.password.value, uiState.email.value) }
                verify { isValidEmail(uiState.email.value) }
                verify { isValidPassword(uiState.password.value) }
            }
        }
}
