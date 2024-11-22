package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey
import java.text.NumberFormat

@Composable
fun <N : Number> NumberEntry(
    key: AccountKey<N>,
    format: NumberFormat,
    convert: (Number) -> N, // TODO: Try to get rid of this convert function
    value: N,
    onValueChanged: (N) -> Unit,
) {
    ListRow(key.name) {
        TextField(
            format.format(value),
            onValueChange = { text ->
                format.parse(text)?.also {
                    onValueChanged(convert(it))
                }
            }
        )
    }
}
