package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
internal fun Paragraph(element: MarkdownElement.Paragraph) {
    BasicText(
        text = element.text,
        style = TextStyles.bodyMedium,
    )
}