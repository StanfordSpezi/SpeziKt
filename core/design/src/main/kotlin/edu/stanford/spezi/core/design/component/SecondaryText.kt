package edu.stanford.spezi.core.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles

@Composable
fun SecondaryText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyles.bodySmall,
        color = Colors.secondary,
    )
}
