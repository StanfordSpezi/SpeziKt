package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import java.util.UUID
import kotlin.reflect.KProperty

interface AccountSetupComponent {
    val id: UUID
    val configuration: IdentityProviderConfiguration
    @get:Composable val view: () -> Unit
}

data class IdentityProvider internal constructor(
    val composable: @Composable () -> Unit,
    val configuration: IdentityProviderConfiguration
    ) {

    operator fun getValue(thisRef: Any, property: KProperty<*>) = composable

    companion object {
        operator fun invoke(
            isEnabled: Boolean = true,
            section: AccountSetupSection = AccountSetupSection.default,
            composable: @Composable () -> Unit
        ): IdentityProvider {
            return IdentityProvider(composable, IdentityProviderConfiguration(isEnabled, section))
        }
    }
}