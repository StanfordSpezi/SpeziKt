package edu.stanford.spezi.module.account.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.coVerifyNever
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.R
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import edu.stanford.spezi.module.account.register.AuthValidator
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkConstructor
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val authenticationManager: AuthenticationManager = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: AuthValidator = mockk()
    private val navigator: Navigator = mockk()
    private val context: Context = mockk()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        with(validator) {
            every { isFormValid(any(), any()) } returns true
            every { isValidEmail(any()) } returns AuthValidator.Result.Valid
            every { isValidPassword(any()) } returns AuthValidator.Result.Valid
        }

        every { navigator.navigateTo(any()) } just Runs

        mockkStatic(Uri::class)
        mockkConstructor(Intent::class)

        loginViewModel = LoginViewModel(
            authenticationManager = authenticationManager,
            messageNotifier = messageNotifier,
            accountEvents = accountEvents,
            navigator = navigator,
            authValidator = validator,
            context = context,
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
        unmockkConstructor(Intent::class)
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
                email = loginViewModel.uiState.value.email.value,
                password = loginViewModel.uiState.value.password.value,
            )

            // When
            loginViewModel.onAction(action)

            // Then
            verify { navigator.navigateTo(expectedNavigationEvent) }
        }

    @Test
    fun `it should handle Email clicked correctly`() {
        // given
        val email = "some@email.com"
        val uri: Uri = mockk()
        every { Uri.parse("mailto:$email") } returns uri
        val intent = mockk<Intent>(relaxed = true)
        every { anyConstructed<Intent>().setAction(Intent.ACTION_SENDTO) } returns intent
        every { anyConstructed<Intent>().setData(uri) } returns intent
        every { anyConstructed<Intent>().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) } returns intent

        every { context.startActivity(intent) } just Runs
        val action = Action.EmailClicked(email)

        // when
        loginViewModel.onAction(action)

        // then
        verify { context.startActivity(intent) }
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
        verify { messageNotifier.notify(R.string.error_sign_in_failed) }
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
            verify { messageNotifier.notify(R.string.email_sent) }
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
            verify { messageNotifier.notify(R.string.failed_to_send_email) }
        }

    @Test
    fun `given ForgotPassword action with invalid email then notify message`() =
        runTestUnconfined {
            // Given
            val action = Action.Async.ForgotPassword
            val email = "test@test.com"
            every { validator.isValidEmail(email) } returns AuthValidator.Result.Invalid("invalid")

            // When
            listOf(
                Action.TextFieldUpdate(email, TextFieldType.EMAIL),
                action,
            ).forEach {
                loginViewModel.onAction(action = it)
            }

            // Then
            coVerifyNever { authenticationManager.sendForgotPasswordEmail(email) }
            verify { messageNotifier.notify(R.string.please_enter_a_valid_email) }
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
        verify { messageNotifier.notify(R.string.error_sign_in_failed) }
    }
}
