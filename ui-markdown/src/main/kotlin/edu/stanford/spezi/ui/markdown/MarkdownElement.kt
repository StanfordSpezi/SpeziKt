package edu.stanford.spezi.ui.markdown

sealed interface MarkdownElement {
    data class Heading(val level: Int, val text: String) : MarkdownElement
    data class Paragraph(val text: String) : MarkdownElement
    data class Bold(val text: String) : MarkdownElement
    data class ListItem(val text: String) : MarkdownElement
}
