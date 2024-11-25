package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.views.display.StringResourceConvertible
import kotlin.enums.EnumEntries

@Composable
fun <Value> EnumEntry(
    key: AccountKey<Value>,
    value: Value,
    values: EnumEntries<Value>,
    onValueChanged: (Value) -> Unit,
) where Value : Enum<Value>, Value : StringResourceConvertible {
    val isExpanded = remember { mutableStateOf(false) }

    ListRow(key.name.text()) {
        Button(onClick = { isExpanded.value = true }) {
            Text(value.stringResource.text())
        }
        DropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }
        ) {
            for (option in values) {
                DropdownMenuItem(
                    text = {
                        Text(option.stringResource.text())
                    },
                    onClick = {
                        onValueChanged(option)
                        isExpanded.value = false
                    }
                )
            }
        }
    }
}
