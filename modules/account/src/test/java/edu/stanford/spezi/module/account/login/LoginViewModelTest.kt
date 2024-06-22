package edu.stanford.spezi.module.account.login

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.cred.manager.CredentialLoginManagerAuth
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val credentialLoginManagerAuth: CredentialLoginManagerAuth = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: LoginFormValidator = LoginFormValidator()
    private val navigator: Navigator = mockk(relaxed = true)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(
            credentialLoginManagerAuth = credentialLoginManagerAuth,
            messageNotifier = messageNotifier,
            accountEvents = accountEvents,
            navigator = navigator,
            validator = validator
        )
        every { navigator.navigateTo(any()) } just Runs
    }

    @Test
    fun `given TextFieldUpdate action when onAction is called then update LoginUiState`() =
        runTestUnconfined {
            // Given
            val email = "test@test.com"
            val action = Action.TextFieldUpdate(email, TextFieldType.EMAIL)

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.email.value).isEqualTo(email)
        }

    @Test
    fun `given TogglePasswordVisibility action when onAction is called then update LoginUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.TogglePasswordVisibility

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.passwordVisibility).isTrue()
        }

    @Test
    fun `given NavigateToRegister action when onAction is called then navigate to RegisterScreen`() =
        runTestUnconfined {
            // Given
            val action = Action.NavigateToRegister
            val expectedNavigationEvent = AccountNavigationEvent.RegisterScreen(
                isGoogleSignUp = false,
                email = loginViewModel.uiState.value.email.value,
                password = loginViewModel.uiState.value.password.value,
            )

            // When
            loginViewModel.onAction(action)

            // Then
            verify { navigator.navigateTo(expectedNavigationEvent) }
        }

    @Test
    fun `given SetIsAlreadyRegistered action when onAction is called then update isAlreadyRegistered in LoginUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.SetIsAlreadyRegistered(true)

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.isAlreadyRegistered).isTrue()
        }

    @Test
    fun `given ForgotPassword action with valid email when onAction is called then send forgot password email`() =
        runTestUnconfined {
            // Given
            val action = Action.ForgotPassword
            val validEmail = "test@test.com"

            // When
            loginViewModel.onAction(Action.TextFieldUpdate(validEmail, TextFieldType.EMAIL))
            loginViewModel.onAction(action)

            // Then
            coVerify { credentialLoginManagerAuth.sendForgotPasswordEmail(validEmail) }
        }

    @Test
    fun `given ForgotPassword action with invalid email when onAction is called then do not send forgot password email`() =
        runTestUnconfined {
            // Given
            val action = Action.ForgotPassword
            val invalidEmail = "invalidEmail"

            // When
            loginViewModel.onAction(Action.TextFieldUpdate(invalidEmail, TextFieldType.EMAIL))
            loginViewModel.onAction(action)

            // Then
            coVerify(exactly = 0) { credentialLoginManagerAuth.sendForgotPasswordEmail(invalidEmail) }
        }
}
