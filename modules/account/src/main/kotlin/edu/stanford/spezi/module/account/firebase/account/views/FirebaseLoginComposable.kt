package edu.stanford.spezi.module.account.firebase.account.views

import androidx.compose.runtime.Composable
import edu.stanford.spezi.module.account.account.views.setup.provider.AccountSetupProviderComposable
import edu.stanford.spezi.module.account.firebase.account.FirebaseAccountService

@Composable
internal fun FirebaseLoginComposable(
    service: FirebaseAccountService
) {
    AccountSetupProviderComposable(
        login = {
            service.login(userId = it.userId, password = it.password)
        },
        signup = {
            service.signUp(it)
        },
        resetPassword = {
            service.resetPassword(it)
        }
    )
}
