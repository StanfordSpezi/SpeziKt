package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme

@Composable
fun MarkdownComponent(markdownText: String) {
    val elements = parseMarkdown(markdownText)
    Column(modifier = Modifier.padding(Spacings.medium)) {
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.Heading -> Heading(element)
                is MarkdownElement.Paragraph -> Paragraph(element)
                is MarkdownElement.Bold -> BoldText(element)
                is MarkdownElement.ListItem -> ListItem(element)
            }
        }
    }
}

@Preview
@Composable
private fun MarkdownPreview() {
    SpeziTheme {
        MarkdownComponent(
            markdownText = """
                            # Markdown Title
                            This is a paragraph in **Markdown**.
                            
                            ## Subtitle
                            - Item 1
                            - Item 2
                            - Item 3
                            
                            ## Another Subtitle
                            **Bold Text**
                            This is a paragraph in Markdown.
                            - Item 1
                            - Item 2
                            - Item 3
            """.trimIndent()
        )
    }
}
