package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.compositionLocal.LocalFollowUpBehavior
import edu.stanford.spezi.module.account.account.compositionLocal.SignUpProviderCompliance
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupComponent
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupSection
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.documentation.EmptyServicesWarning

sealed interface AccountSetupState {
    data object Generic : AccountSetupState
    data object SetupShown : AccountSetupState
    data class RequiringAdditionalInfo(val keys: List<AccountKey<*>>) : AccountSetupState
    data object LoadingExistingAccount : AccountSetupState

    companion object {
        val DefaultValue = AccountSetupState.Generic
    }
}

val LocalAccountSetupState = compositionLocalOf<AccountSetupState> { AccountSetupState.Generic }

@Composable
fun AccountSetup(
    setupComplete: suspend (AccountDetails) -> Unit = {},
    header: @Composable () -> Unit,
    done: @Composable () -> Unit,
) {
    val setupState = remember { mutableStateOf(AccountSetupState.Generic) }
    val compliance = remember { mutableStateOf(null as SignUpProviderCompliance?) }
    val followUpSheet = remember { mutableStateOf(false) }
    val isCompletingSetup = remember { mutableStateOf(false) }

    val account = LocalAccount.current
    val followUpBehavior = LocalFollowUpBehavior.current

    val hasSetupComponents = account?.accountSetupComponents?.any { it.configuration.isEnabled } ?: false

    if (!hasSetupComponents) {
        EmptyServicesWarning()
    } else {
        Column(Modifier.padding(horizontal = Spacings.medium)) {
            val categorized = mutableMapOf<AccountSetupSection, AccountSetupComponent>()
            for (component in account?.accountSetupComponents ?: emptyList()) {
                if (component.configuration.isEnabled) {
                    categorized[component.configuration.section] = component.component
                }
            }

            val lastKey = categorized.keys.lastOrNull()
            for (entry in categorized) {
                entry.value.content()

                if (entry.key != lastKey) {
                    ServicesDivider()
                }
            }
        }
    }
}
