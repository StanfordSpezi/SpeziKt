package edu.stanford.spezi.modules.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.TextStyles

@Composable
fun SecondaryText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyles.bodySmall,
        color = Colors.secondary,
    )
}
