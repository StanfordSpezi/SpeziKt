package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import java.util.UUID
import kotlin.reflect.KProperty

interface AccountSetupComponent {
    val uuid: UUID
    val configuration: IdentityProviderConfiguration

    @Composable
    fun Content()
}

data class IdentityProvider internal constructor(
    val content: @Composable () -> Unit,
    val configuration: IdentityProviderConfiguration,
) {

    operator fun getValue(thisRef: Any, property: KProperty<*>) = content

    companion object {
        operator fun invoke(
            isEnabled: Boolean = true,
            section: AccountSetupSection = AccountSetupSection.default,
            content: @Composable () -> Unit,
        ): IdentityProvider {
            return IdentityProvider(content, IdentityProviderConfiguration(isEnabled, section))
        }
    }
}
