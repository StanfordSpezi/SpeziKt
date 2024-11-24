package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.compositionLocal.LocalPreferredSetupStyle
import edu.stanford.spezi.module.account.account.compositionLocal.PreferredSetupStyle
import edu.stanford.spezi.module.account.account.model.UserIdPasswordCredential
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

// Generic constraint doesn't seem to be used at all (SignUpView is generic, but internal and only used with UserIdCredential).
internal sealed interface PresentedSetupStyle {
    data object SignUp : PresentedSetupStyle
    data class Login(val login: suspend (UserIdPasswordCredential) -> Unit) : PresentedSetupStyle
}

@Composable
fun AccountSetupProviderComposable(
    login: suspend (UserIdPasswordCredential) -> Unit,
    signup: suspend (AccountDetails) -> Unit,
    resetPassword: suspend (String) -> Unit,
) {
    AccountSetupProviderComposable(
        login = login,
        signup = {
        },
        passwordReset = {
            Text("Password Reset")
        }
    )
    TODO("Not implemented yet")
}

@Composable
fun AccountSetupProviderComposable(
    login: (suspend (UserIdPasswordCredential) -> Unit)? = null,
    signup: (@Composable () -> Unit)? = null,
    passwordReset: (@Composable () -> Unit)? = null,
) {
    val preferredSetupStyle = LocalPreferredSetupStyle.current
    val presentedStyle = remember { mutableStateOf<PresentedSetupStyle>(PresentedSetupStyle.SignUp) }
    val presentingSignup = remember { mutableStateOf(false) }

    LaunchedEffect(preferredSetupStyle) {
        when (preferredSetupStyle) {
            PreferredSetupStyle.AUTOMATIC, PreferredSetupStyle.LOGIN -> {
                login?.let {
                    presentedStyle.value = PresentedSetupStyle.Login(it)
                } ?: run {
                    assert(signup != null) { "AccountSetupProviderComposable must either support login or signup or both." }
                    presentedStyle.value = PresentedSetupStyle.SignUp
                }
            }
            PreferredSetupStyle.SIGNUP -> {
                signup?.let {
                    presentedStyle.value = PresentedSetupStyle.SignUp
                } ?: run {
                    login?.let {
                        presentedStyle.value = PresentedSetupStyle.Login(it)
                    } ?: assert(false) { "AccountSetupProviderComposable must either support login or signup or both." }
                }
            }
        }
    }

    if (presentingSignup.value) {
        Text("Signup Sheet")
    }

    val presentedStyleValue = presentedStyle.value
    when (presentedStyleValue) {
        PresentedSetupStyle.SignUp -> {
            /*
            SignupSetup(
                presentedStyle,
                login,
                presentingSignup
            )
             */
        }
        is PresentedSetupStyle.Login -> {
            LoginSetup(
                login = presentedStyleValue.login,
                passwordReset = passwordReset,
                supportsSignup = signup != null,
                presentingSignup = presentingSignup
            )
        }
    }

    TODO("Sheet for signup form")
}

@ThemePreviews
@Composable
private fun AccountSetupProviderComposablePreview() {
    SpeziTheme(isPreview = true) {
        Text("Hello")
    }
}
