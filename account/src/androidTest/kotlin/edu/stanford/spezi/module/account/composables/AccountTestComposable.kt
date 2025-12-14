package edu.stanford.spezi.module.account.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.AccountOverview
import edu.stanford.spezi.module.account.account.AccountOverviewCloseBehavior
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.utils.TestStandard
import edu.stanford.spezi.module.account.utils.invitationCode
import javax.inject.Inject

@HiltViewModel
class AccountOverviewTestViewModel @Inject constructor(
    val standard: TestStandard,
    val account: Account,
) : ViewModel()

@Composable
fun AccountTestComposable() {
    val viewModel = hiltViewModel<AccountOverviewTestViewModel>()

    CompositionLocalProvider(LocalAccount provides viewModel.account) {
        Column {
            Text("AccountOverviewTestComposable")
            Header(
                viewModel.account,
                viewModel.standard,
            )
            OverviewSheet(viewModel.account)
        }
    }
}

@Composable
private fun Header(
    account: Account,
    standard: TestStandard,
) {
    val accountIdFromAnonymousUser = remember { mutableStateOf<String?>(null) }
    account.details?.let { details ->
        ListRow("User Id") {
            if (details.isAnonymous) {
                Text("Anonymous")

                LaunchedEffect(Unit) {
                    accountIdFromAnonymousUser.value = details.accountId
                }
            } else {
                Text(details.userId)
            }
        }
        accountIdFromAnonymousUser.value?.let { accountId ->
            ListRow("Account Id") {
                if (details.accountId == accountId) {
                    Text("Stable", color = Color.Green)
                } else {
                    Text("Changed", color = Color.Red)
                }
            }
        }
    }

    if (standard.deleteNotified) {
        Text("Got notified about deletion!")
    }
}

@Composable
private fun OverviewSheet(account: Account) {
    AccountOverview(closeBehavior = AccountOverviewCloseBehavior.SHOW_CLOSE_BUTTON) {
        item {
            Text("License Information")
        }

        account.details?.invitationCode?.let {
            item {
                Text("Invitation Code: $it")
            }
        }
    }
}
