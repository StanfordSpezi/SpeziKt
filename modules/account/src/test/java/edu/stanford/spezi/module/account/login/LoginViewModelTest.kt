package edu.stanford.spezi.module.account.login

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.coVerifyNever
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import edu.stanford.spezi.module.account.register.FormValidator
import io.mockk.Runs
import io.mockk.coEvery
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
    private val authenticationManager: AuthenticationManager = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: LoginFormValidator = mockk()
    private val navigator: Navigator = mockk()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        with(validator) {
            every { isFormValid(any()) } returns true
            every { isValidEmail(any()) } returns FormValidator.Result.Valid
            every { isValidPassword(any()) } returns FormValidator.Result.Valid
        }

        every { navigator.navigateTo(any()) } just Runs
        loginViewModel = LoginViewModel(
            authenticationManager = authenticationManager,
            messageNotifier = messageNotifier,
            accountEvents = accountEvents,
            navigator = navigator,
            validator = validator
        )
    }

    @Test
    fun `it should update email correctly`() =
        runTestUnconfined {
            // Given
            val email = "test@test.com"
            val action = Action.TextFieldUpdate(email, TextFieldType.EMAIL)

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.email.value).isEqualTo(email)
            assertThat(uiState.isFormValid).isTrue()
            assertThat(uiState.isPasswordSignInEnabled)
                .isEqualTo(email.isNotEmpty() && uiState.password.value.isNotEmpty())
        }

    @Test
    fun `it should update password correctly`() =
        runTestUnconfined {
            // Given
            val password = "top-secret"
            val action = Action.TextFieldUpdate(password, TextFieldType.PASSWORD)

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.password.value).isEqualTo(password)
            assertThat(uiState.isFormValid).isTrue()
            assertThat(uiState.isPasswordSignInEnabled)
                .isEqualTo(password.isNotEmpty() && uiState.email.value.isNotEmpty())
        }

    @Test
    fun `given TogglePasswordVisibility action when onAction is called then update LoginUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.TogglePasswordVisibility
            val initialVisibility = loginViewModel.uiState.value.passwordVisibility

            // When
            loginViewModel.onAction(action)

            // Then
            val uiState = loginViewModel.uiState.value
            assertThat(uiState.passwordVisibility).isEqualTo(initialVisibility.not())
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
    fun `it should handle successful google sign in correctly`() = runTestUnconfined {
        // given
        coEvery { authenticationManager.signInWithGoogle() } returns Result.success(Unit)

        // when
        loginViewModel.onAction(Action.Async.GoogleSignInOrSignUp)

        // then
        verify { accountEvents.emit(event = AccountEvents.Event.SignInSuccess) }
    }

    @Test
    fun `it should failure google sign in correctly`() = runTestUnconfined {
        // given
        coEvery {
            authenticationManager.signInWithGoogle()
        } returns Result.failure(Exception("Failed to sign in"))

        // when
        loginViewModel.onAction(Action.Async.GoogleSignInOrSignUp)

        // then
        verify { accountEvents.emit(event = AccountEvents.Event.SignInFailure) }
        verify { messageNotifier.notify(message = "Failed to sign in") }
    }

    @Test
    fun `it should handle google sign up correctly`() = runTestUnconfined {
        // given
        val uiState = loginViewModel.uiState.value
        val expectedEvent = AccountNavigationEvent.RegisterScreen(
            isGoogleSignUp = true,
            email = uiState.email.value,
            password = uiState.password.value,
        )
        coEvery {
            authenticationManager.signInWithGoogle()
        } returns Result.failure(Exception("Failed to sign in"))

        // when
        loginViewModel.onAction(Action.Async.GoogleSignInOrSignUp)

        // then
        verify { navigator.navigateTo(expectedEvent) }
    }

    @Test
    fun `given ForgotPassword action with valid email when onAction is called then send forgot password email`() =
        runTestUnconfined {
            // Given
            val action = Action.Async.ForgotPassword
            val validEmail = "test@test.com"
            coEvery {
                authenticationManager.sendForgotPasswordEmail(validEmail)
            } returns Result.success(Unit)

            // When
            loginViewModel.onAction(Action.TextFieldUpdate(validEmail, TextFieldType.EMAIL))
            loginViewModel.onAction(action)

            // Then
            coVerify { authenticationManager.sendForgotPasswordEmail(validEmail) }
            verify { messageNotifier.notify("Email sent") }
        }

    @Test
    fun `given ForgotPassword action with valid email when onAction is called, notifies message in case email sending failed`() =
        runTestUnconfined {
            // Given
            val action = Action.Async.ForgotPassword
            val validEmail = "test@test.com"
            coEvery {
                authenticationManager.sendForgotPasswordEmail(validEmail)
            } returns Result.failure(Exception("Failure"))

            // When
            loginViewModel.onAction(Action.TextFieldUpdate(validEmail, TextFieldType.EMAIL))
            loginViewModel.onAction(action)

            // Then
            coVerify { authenticationManager.sendForgotPasswordEmail(validEmail) }
            verify { messageNotifier.notify("Failed to send email") }
        }

    @Test
    fun `given ForgotPassword action with invalid email then notify message`() =
        runTestUnconfined {
            // Given
            val action = Action.Async.ForgotPassword
            val email = "test@test.com"
            every { validator.isValidEmail(email) } returns FormValidator.Result.Invalid("invalid")

            // When
            listOf(
                Action.TextFieldUpdate(email, TextFieldType.EMAIL),
                action,
            ).forEach {
                loginViewModel.onAction(action = it)
            }

            // Then
            coVerifyNever { authenticationManager.sendForgotPasswordEmail(email) }
            verify { messageNotifier.notify("Please enter a valid email") }
        }

    @Test
    fun `it should handle successful PasswordSignInOrSignUp correctly`() {
        // given
        val email = "test@test.com"
        val password = "123456"
        listOf(
            Action.TextFieldUpdate(email, TextFieldType.EMAIL),
            Action.TextFieldUpdate(password, TextFieldType.PASSWORD),
        ).forEach {
            loginViewModel.onAction(it)
        }
        coEvery {
            authenticationManager.signIn(email = email, password = password)
        } returns Result.success(Unit)
        val action = Action.Async.PasswordSignIn

        // when
        loginViewModel.onAction(action)

        // then
        verify { accountEvents.emit(AccountEvents.Event.SignInSuccess) }
    }

    @Test
    fun `it should handle failure PasswordSignInOrSignUp correctly`() {
        // given
        val email = "test@test.com"
        val password = "123456"
        listOf(
            Action.TextFieldUpdate(email, TextFieldType.EMAIL),
            Action.TextFieldUpdate(password, TextFieldType.PASSWORD),
        ).forEach { loginViewModel.onAction(it) }
        coEvery {
            authenticationManager.signIn(email = email, password = password)
        } returns Result.failure(Exception("Failure"))
        val action = Action.Async.PasswordSignIn

        // when
        loginViewModel.onAction(action)

        // then
        verify { accountEvents.emit(AccountEvents.Event.SignInFailure) }
        verify { messageNotifier.notify(message = "Failed to sign in") }
    }
}
