package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.module.account.account.compositionLocal.AccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountViewType
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.DisplayWithStoredValue
import edu.stanford.spezi.module.account.account.value.EntryWithEmptyValue
import edu.stanford.spezi.module.account.account.value.EntryWithStoredOrInitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement

@Composable
internal fun AccountKeyOverviewRow(
    key: AccountKey<*>,
    details: AccountDetails,
    isEditing: MutableState<Boolean>,
    viewModel: AccountOverviewFormViewModel,
    modifier: Modifier = Modifier,
) {
    // TODO: Make use of isDeleteDisabled!
    val isDeleteDisabled = remember {
        if (details.contains(key) && viewModel.containsRemovedKey(key)) {
            viewModel.requirement(key) == AccountKeyRequirement.REQUIRED
        } else {
            viewModel.containsAddedKey(key)
        }
    }

    Box(modifier) {
        if (isEditing.value) {
            Row {
                if (details.contains(key) && !viewModel.containsRemovedKey(key)) {
                    val accountViewType = AccountViewType.Overview(
                        AccountViewType.OverviewEntryMode.EXISTING
                    )
                    CompositionLocalProvider(
                        LocalAccountViewType provides accountViewType
                    ) {
                        // TODO:
                        //  if let view = accountKey
                        //      .dataEntryViewFromBuilder(
                        //          builder: model.modifiedDetailsBuilder
                        //      )
                        key.EntryWithStoredOrInitialValue(details)
                    }
                } else if (viewModel.containsAddedKey(key)) {
                    val accountViewType = AccountViewType.Overview(
                        AccountViewType.OverviewEntryMode.NEW
                    )
                    CompositionLocalProvider(
                        LocalAccountViewType provides accountViewType
                    ) {
                        key.EntryWithEmptyValue()
                    }
                } else {
                    Button(onClick = {
                        viewModel.addAccountDetail(key)
                    }) {
                        Text("VALUE_ADD ${key.name}")
                    }
                }
            }
        } else {
            val accountViewType = AccountViewType.Overview(
                AccountViewType.OverviewEntryMode.DISPLAY
            )
            CompositionLocalProvider(
                LocalAccountViewType provides accountViewType
            ) {
                key.DisplayWithStoredValue(details)
            }
        }
    }
}
