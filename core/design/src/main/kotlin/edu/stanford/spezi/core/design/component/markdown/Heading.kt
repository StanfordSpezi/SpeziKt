package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
internal fun Heading(element: MarkdownElement.Heading) {
    BasicText(
        text = element.text,
        style = when (element.level) {
            1 -> TextStyles.headlineLarge
            2 -> TextStyles.headlineMedium
            3 -> TextStyles.titleMedium
            else -> TextStyles.titleMedium
        },
    )
}
