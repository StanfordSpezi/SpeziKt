package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
internal fun ListItem(element: MarkdownElement.ListItem) {
    Row {
        BasicText(
            text = "    - ",
            style = TextStyles.bodyMedium
        )
        BasicText(
            text = element.text,
            style = TextStyles.bodyMedium
        )
    }
}
