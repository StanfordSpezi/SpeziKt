package edu.stanford.spezi.core.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ListRow(
    label: StringResource,
    content: @Composable () -> Unit,
) {
    ListRow({ Text(label.text()) }) { }
}

@Composable
fun ListRow(
    label: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    TODO("Not implemented yet")
}
