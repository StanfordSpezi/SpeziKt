package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.utils.UUID
import java.util.UUID
import kotlin.reflect.KProperty

data class AccountSetupComponent internal constructor(
    val uuid: UUID = UUID(),
    val configuration: IdentityProviderConfiguration,
    val content: @Composable () -> Unit,
)

data class IdentityProvider internal constructor(
    val configuration: IdentityProviderConfiguration,
    val content: @Composable () -> Unit,
) {
    constructor(
        isEnabled: Boolean = true,
        section: AccountSetupSection = AccountSetupSection.default,
        content: @Composable () -> Unit,
    ) : this(
        IdentityProviderConfiguration(isEnabled, section),
        content,
    )

    operator fun getValue(thisRef: Any, property: KProperty<*>) = content

    val component = AccountSetupComponent(configuration = configuration, content = content)

    var isEnabled: Boolean
        get() = configuration.isEnabled
        set(value) { configuration.isEnabled = value }
}
