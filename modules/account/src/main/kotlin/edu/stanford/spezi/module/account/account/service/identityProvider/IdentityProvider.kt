package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import java.util.UUID

interface AccountSetupComponent {
    val id: UUID
    val configuration: IdentityProviderConfiguration
    @get:Composable val view: () -> Unit
}

data class IdentityProvider internal constructor(
    val composable: @Composable () -> Unit,
    val configuration: IdentityProviderConfiguration
    ) {

    companion object {
        operator fun invoke(
            composable: @Composable () -> Unit,
            isEnabled: Boolean = true,
            section: AccountSetupSection = AccountSetupSection.default
        ): IdentityProvider {
            return IdentityProvider(composable, IdentityProviderConfiguration(isEnabled, section))
        }
    }
}