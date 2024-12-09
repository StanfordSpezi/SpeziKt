package edu.stanford.spezi.module.account.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.views.documentation.MissingAccountDetailsWarning
import edu.stanford.spezi.module.account.account.views.overview.AccountOverviewSections

enum class AccountOverviewCloseBehavior {
    DISABLED, SHOW_CLOSE_BUTTON
}

enum class AccountDeletionBehavior {
    DISABLED, EDIT_MODE, BELOW_LOGOUT
}

@Composable
fun AccountOverview(
    closeBehavior: AccountOverviewCloseBehavior = remember { AccountOverviewCloseBehavior.DISABLED },
    deletionBehavior: AccountDeletionBehavior = remember { AccountDeletionBehavior.EDIT_MODE },
    additionalSections: LazyListScope.() -> Unit,
) {
    // val viewModel = hiltViewModel<AccountOverviewFormViewModel>()
    val account = LocalAccount.current

    Column {
        account?.details?.let { details ->
            AccountOverviewSections(
                account = account,
                details = details,
                closeBehavior = closeBehavior,
                deletionBehavior = deletionBehavior,
                additionalSections = additionalSections
            )
            // TODO: Missing Form wrapper and padding
        } ?: run {
            MissingAccountDetailsWarning()
        }
    }
}

@ThemePreviews
@Composable
fun AccountOverviewPreview() {
    SpeziTheme {
        AccountOverview { }
    }
}
