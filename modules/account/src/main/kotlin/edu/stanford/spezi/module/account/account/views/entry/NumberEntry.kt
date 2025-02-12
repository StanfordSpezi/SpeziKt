package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.module.account.account.value.AccountKey
import java.text.NumberFormat

@Composable
fun <N : Number> NumberEntry(
    key: AccountKey<N>,
    format: NumberFormat,
    convert: (Number) -> N, // TODO: Try to get rid of this convert function
    value: N,
    modifier: Modifier = Modifier,
    onValueChanged: (N) -> Unit,
) {
    val text = remember { mutableStateOf(format.format(value)) }
    ListRow(key.name.text(), modifier) {
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
                format.parse(text.value)?.also {
                    onValueChanged(convert(it))
                }
            }
        )
    }
}
