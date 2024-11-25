package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ListRow(
        modifier = modifier,
        label = { Text(label) },
        content = content
    )
}

@Composable
fun ListRow(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier) {
        label()
        Spacer(Modifier.fillMaxWidth())
        content()
    }
}
