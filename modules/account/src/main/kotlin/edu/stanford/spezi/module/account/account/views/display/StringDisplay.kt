package edu.stanford.spezi.module.account.account.views.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey

@Composable
fun StringDisplay(
    key: AccountKey<String>,
    value: String,
) {
    ListRow(key.name) {
        Text(value)
    }
}
