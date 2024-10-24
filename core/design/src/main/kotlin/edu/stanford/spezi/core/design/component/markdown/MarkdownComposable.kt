package edu.stanford.spezi.core.design.component.markdown

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun MarkdownComposable(data: suspend () -> ByteArray) {
    // TODO: Figure out why hiltViewModel is not working and how one would do that anyways
    val viewModel = remember { MarkdownViewModel(data, MarkdownParser()) }
    val uiState = viewModel.uiState.collectAsState()

    MarkdownComponent(uiState.value.elements ?: emptyList())
}

@Composable
fun MarkdownComponent(markdownElements: List<MarkdownElement>) {
    LazyColumn(modifier = Modifier.padding(Spacings.medium)) {
        items(markdownElements) { element ->
            when (element) {
                is MarkdownElement.Heading -> Heading(element)
                is MarkdownElement.Paragraph -> Paragraph(element)
                is MarkdownElement.Bold -> BoldText(element)
                is MarkdownElement.ListItem -> ListItem(element)
            }
        }
    }
}

@Composable
private fun Heading(element: MarkdownElement.Heading) {
    Text(
        text = element.text,
        style = when (element.level) {
            1 -> TextStyles.headlineLarge
            2 -> TextStyles.headlineMedium
            3 -> TextStyles.titleMedium
            else -> TextStyles.titleMedium
        },
    )
}

@Composable
private fun Paragraph(element: MarkdownElement.Paragraph) {
    Text(
        text = element.text,
        style = TextStyles.bodyMedium,
    )
}

@Composable
private fun BoldText(element: MarkdownElement.Bold) {
    Text(
        text = element.text,
        style = TextStyles.bodyMedium.copy(fontWeight = FontWeight.Bold),
    )
}

@Composable
private fun ListItem(element: MarkdownElement.ListItem) {
    Row {
        Text(
            text = "    - ",
            style = TextStyles.bodyMedium
        )
        Text(
            text = element.text,
            style = TextStyles.bodyMedium
        )
    }
}

@Preview
@Composable
private fun MarkdownPreview() {
    SpeziTheme {
        val elements = remember {
            MarkdownParser().parse(
                """
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
        MarkdownComponent(markdownElements = elements)
    }
}
