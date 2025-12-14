package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import edu.stanford.spezi.module.account.account.compositionLocal.AccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountViewType
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.value.keys.userIdType

@Composable
internal fun NameOverview(
    viewModel: AccountOverviewFormViewModel,
    details: AccountDetails,
    modifier: Modifier = Modifier,
) {
    val keys = viewModel.namesOverviewKeys(details)

    // TODO: This is most likely needed on the detail page, not here
    CompositionLocalProvider(LocalAccountViewType provides AccountViewType.Overview(AccountViewType.OverviewEntryMode.DISPLAY)) {
        Column(modifier = modifier) {
            for (key in keys) {
                val name = if (key == AccountKeys.userId) {
                    details.userIdType.stringResource
                } else {
                    key.name
                }

                Row(modifier = Modifier.clickable {
                    TODO("""
                    Open page: wrapper.accountKey.singleEditView(model: model, details: accountDetails)
                        .anyModifiers(account.securityRelatedModifiers.map { \$0.anyViewModifier })
                """)
                }) {
                    Text(name.text())
                    Spacer(Modifier.weight(1f))
                    Text("VALUE_ADD ${name.text()}")
                }

                // key.SingleEntry(viewModel, details)
            }
        }
    }

    // TODO: .navigationTitle(model.accountIdentifierLabel(configuration: account.configuration, accountDetails))
}
