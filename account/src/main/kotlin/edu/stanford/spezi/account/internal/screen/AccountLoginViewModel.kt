package edu.stanford.spezi.account.internal.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AuthProvider
import edu.stanford.spezi.account.UserIdPasswordCredential
import edu.stanford.spezi.account.authProviders
import edu.stanford.spezi.account.fieldValidationRules
import edu.stanford.spezi.account.userIdConfiguration
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.EventSink
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.MutableSpeziScaffoldState
import edu.stanford.spezi.ui.SpeziInputField
import edu.stanford.spezi.ui.SpeziScaffoldState
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.AccountLoginLayout
import edu.stanford.spezi.ui.account.AlternativeSignIn
import edu.stanford.spezi.ui.account.ForgotPasswordLink
import edu.stanford.spezi.ui.account.LabeledHorizontalDivider
import edu.stanford.spezi.ui.account.SignUpLink
import edu.stanford.spezi.ui.coroutinesLauncher
import edu.stanford.spezi.ui.showErrorToast
import edu.stanford.spezi.ui.speziAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 *
 * Drives the login form state, handles user ID and password validation, and delegates
 * sign-in, password reset, and alternative provider actions to [AccountService].
 * Exposes [events] for the UI to react to successful sign-in or dismissal, and opens
 * the sign-up sheet on demand via [AccountSheetController].
 */
internal class AccountLoginViewModel(
    private val accountService: AccountService,
    private val accountSheetController: AccountSheetController,
) : ViewModel() {
    private val configuration = accountService.configuration

    private val eventSink = EventSink<AccountLoginEvent>()
    private val scaffoldState = MutableSpeziScaffoldState(
        coroutinesLauncher = coroutinesLauncher,
        appBar = speziAppBar {
            close { eventSink.push(AccountLoginEvent.Dismissed) }
        }
    )

    private val layoutState = MutableStateFlow(getLayout())
    private val userId get() = layoutState.value.userIdInput.value
    private val password get() = layoutState.value.passwordInput.value

    val screen = AccountLoginScreen(
        scaffoldState = scaffoldState,
        layout = layoutState,
    )

    val events = eventSink.source()

    private fun getLayout(): AccountLoginLayout {
        val authProviders = configuration.authProviders

        val alternativeSignIn = if (authProviders.isNotEmpty()) {
            AlternativeSignIn(
                divider = LabeledHorizontalDivider(label = StringResource(Strings.account_login_or_divider)),
                buttons = authProviders.map { provider ->
                    AsyncTextButton(
                        title = provider.actionName,
                        icon = provider.icon,
                        action = { onProviderClicked(provider) },
                    )
                },
            )
        } else {
            null
        }

        return AccountLoginLayout(
            title = StringResource(Strings.account_login_title),
            description = StringResource(Strings.account_login_description),
            userIdInput = SpeziInputField(
                value = "",
                placeholder = userIdLabel(),
                onValueChanged = ::onUserIdChanged,
            ),
            passwordInput = SpeziInputField(
                value = "",
                hideContent = true,
                placeholder = StringResource(Strings.account_login_password_placeholder),
                onValueChanged = ::onPasswordChanged,
            ),
            forgotPasswordLink = ForgotPasswordLink(
                text = StringResource(Strings.account_login_forgot_password),
                onClick = ::onForgotPasswordClicked,
            ),
            loginButton = AsyncTextButton(
                title = StringResource(Strings.account_login_button),
                action = ::onLoginClicked,
            ),
            signUpLink = SignUpLink(
                infoText = StringResource(Strings.account_login_sign_up_info),
                signUpText = StringResource(Strings.account_login_sign_up),
                onClick = ::onSignUpClicked,
            ),
            alternativeSignIn = alternativeSignIn,
        )
    }

    private fun onUserIdChanged(value: String) {
        layoutState.update { it.copy(userIdInput = it.userIdInput.copy(value = value)) }
    }

    private fun onPasswordChanged(value: String) {
        layoutState.update { it.copy(passwordInput = it.passwordInput.copy(value = value)) }
    }

    private fun onForgotPasswordClicked() {
        viewModelScope.launch {
            val userIdError = validateUserId()
            if (userIdError != null) {
                showErrorToast(message = StringResource(Strings.account_login_invalid_user_id_prefix) + userIdLabel())
                return@launch
            }
            accountService.onPasswordForgotten(userId)
                .onSuccess {
                    scaffoldState.showToast(
                        imageResource = ImageResource(Icons.Default.Check),
                        message = StringResource(Strings.account_login_password_reset_success)
                    )
                }.onFailure {
                    showErrorToast(message = StringResource(Strings.account_login_password_reset_failed))
                }
        }
    }

    private fun onLoginClicked() {
        val userIdError = validateUserId()
        val passwordError = validatePassword()
        if (userIdError != null || passwordError != null) {
            layoutState.update {
                it.copy(
                    userIdInput = it.userIdInput.copy(validationMessage = userIdError),
                    passwordInput = it.passwordInput.copy(validationMessage = passwordError),
                )
            }
            return
        }
        viewModelScope.launch {
            accountService.login(credential = UserIdPasswordCredential(userId = userId, password = password))
                .onSuccess { eventSink.push(AccountLoginEvent.Success) }
                .onFailure { showErrorToast(message = StringResource(Strings.account_login_failed)) }
        }
    }

    private fun showErrorToast(message: StringResource) {
        scaffoldState.showErrorToast(message = message)
    }

    private suspend fun onProviderClicked(provider: AuthProvider) {
        accountService.signIn(provider)
            .onSuccess { eventSink.push(AccountLoginEvent.Success) }
            .onFailure { showErrorToast(message = StringResource(Strings.account_login_sign_in_failed)) }
    }

    private fun onSignUpClicked() {
        val screen = accountSheetController.getSheet(
            coroutinesLauncher = coroutinesLauncher,
            request = AccountSheetRequest.SignUp(
                onEvent = { event ->
                    scaffoldState.dismissBottomSheet()
                    when (event) {
                        AccountSheetEvent.Success -> eventSink.push(AccountLoginEvent.Success)
                        AccountSheetEvent.Dismissed -> Unit
                    }
                }
            )
        )
        scaffoldState.showBottomSheet(screen)
    }

    private fun validateUserId(): StringResource? {
        return validate(layoutState.value.userIdInput.value, AccountKeys.userId)
    }

    private fun validatePassword(): StringResource? {
        return validate(value = layoutState.value.passwordInput.value, key = AccountKeys.password)
    }

    private fun validate(value: String, key: AccountKey<String>): StringResource? {
        return configuration.fieldValidationRules(key::class)?.firstNotNullOfOrNull { it.validate(value)?.message }
    }

    private fun userIdLabel(): StringResource = configuration.userIdConfiguration.idType.label
}

internal data class AccountLoginScreen(
    val scaffoldState: SpeziScaffoldState,
    val layout: StateFlow<AccountLoginLayout>,
)

internal sealed interface AccountLoginEvent {
    data object Dismissed : AccountLoginEvent
    data object Success : AccountLoginEvent
}
