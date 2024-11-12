package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

@Composable
internal fun AccountOverviewSections(
    account: Account,
    details: AccountDetails,
    closeBehavior: AccountOverviewCloseBehavior,
    deletionBehavior: AccountDeletionBehavior,
    additionalSections: @Composable () -> Unit,
) {
    val isEditing = remember { mutableStateOf(false) }

    val showDeleteButton = when (deletionBehavior) {
        AccountDeletionBehavior.DISABLED -> false
        AccountDeletionBehavior.EDIT_MODE -> isEditing.value
        AccountDeletionBehavior.BELOW_LOGOUT -> true
    }

    val showLogoutButton =
        if (deletionBehavior == AccountDeletionBehavior.EDIT_MODE) {
            !isEditing.value
        } else { true }
}
