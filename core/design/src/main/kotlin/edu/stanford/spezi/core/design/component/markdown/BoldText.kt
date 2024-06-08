package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
internal fun BoldText(element: MarkdownElement.Bold) {
    BasicText(
        text = element.text,
        style = TextStyles.bodyMedium.copy(fontWeight = FontWeight.Bold),
    )
}
