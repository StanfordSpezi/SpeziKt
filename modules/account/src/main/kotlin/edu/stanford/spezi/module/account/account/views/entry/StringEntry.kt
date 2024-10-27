package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey

@Composable
fun StringEntry(
    key: AccountKey<String>,
    state: MutableState<String>
) {
    ListRow(key.name) {
        TextField(
            state.value,
            onValueChange = { state.value = it }
        )
        // TODO: Use verifiable text field instead
    }
}
