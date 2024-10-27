package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey

@Composable
fun BooleanEntry(
    key: AccountKey<Boolean>,
    state: MutableState<Boolean>
) {
    ListRow(key.name) {
        Switch(
            checked = state.value,
            onCheckedChange = { state.value = it }
        )
    }
}
