package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey

@Composable
fun BooleanEntry(
    key: AccountKey<Boolean>,
    value: Boolean,
    modifier: Modifier = Modifier,
    onValueChanged: (Boolean) -> Unit,
) {
    ListRow(key.name.text(), modifier) {
        Switch(
            checked = value,
            onCheckedChange = onValueChanged
        )
    }
}
