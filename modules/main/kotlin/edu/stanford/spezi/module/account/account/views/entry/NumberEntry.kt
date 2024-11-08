package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey
import java.text.NumberFormat

@Composable
fun <N : Number> NumberEntry(
    key: AccountKey<N>,
    format: NumberFormat,
    convert: (Number) -> N, // TODO: Try to get rid of this convert function
    state: MutableState<N>,
) {
    ListRow(key.name) {
        TextField(
            format.format(state.value),
            onValueChange = { text ->
                format.parse(text)?.also {
                    state.value = convert(it)
                }
            }
        )
    }
}
