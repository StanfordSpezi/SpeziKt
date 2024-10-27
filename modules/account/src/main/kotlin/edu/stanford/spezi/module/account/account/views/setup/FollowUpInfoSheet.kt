package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.runtime.Composable
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications

enum class FollowUpInfoSheetCancelBehavior {
    DISABLED, REQUIRE_LOGOUT, CANCEL
}

@Composable
fun FollowUpInfoSheet(
    keys: List<AccountKey<*>>,
    cancelBehavior: FollowUpInfoSheetCancelBehavior = FollowUpInfoSheetCancelBehavior.REQUIRE_LOGOUT,
    onComplete: (AccountModifications) -> Unit = {}
) {
    TODO("Implement UI")
}