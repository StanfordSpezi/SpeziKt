package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.module.account.account.compositionLocal.SignUpProviderCompliance
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

sealed interface AccountSetupState {
    data object Generic: AccountSetupState
    data object SetupShown: AccountSetupState
    data class RequiringAdditionalInfo(val keys: List<AccountKey<*>>): AccountSetupState
    data object LoadingExistingAccount: AccountSetupState

    companion object {
        val DefaultValue = AccountSetupState.Generic
    }
}

@Composable
fun AccountSetup(
    setupComplete: suspend (AccountDetails) -> Unit = {},
    header: @Composable () -> Unit,
    done: @Composable () -> Unit
) {
    val setupState = remember { mutableStateOf(AccountSetupState.Generic) }
    val compliance = remember { mutableStateOf(null as SignUpProviderCompliance?) }
    val followUpSheet = remember { mutableStateOf(false) }
    val isCompletingSetup = remember { mutableStateOf(false) }

    TODO("Not implemented yet")
}