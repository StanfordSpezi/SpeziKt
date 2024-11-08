package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.runtime.Composable
import edu.stanford.spezi.module.account.account.model.UserIdPasswordCredential

@Composable
fun AccountSetupProviderComposable(
    login: (suspend (UserIdPasswordCredential) -> Unit)? = null,
    signUp: @Composable () -> Unit = {},
    passwordReset: @Composable () -> Unit = {}
) {
    TODO("Not implemented yet")
}
