package edu.stanford.spezi.module.account.account.views.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import java.text.NumberFormat

@Composable
fun <N : Number> NumberDisplay(
    key: AccountKey<N>,
    value: N,
    format: NumberFormat = NumberFormat.getInstance(),
    unit: StringResource = StringResource(""),
) {
    ListRow(key.name) {
        Text(format.format(value) + unit.text())
    }
}
