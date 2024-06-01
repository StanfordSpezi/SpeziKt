package edu.stanford.spezi.core.design.component.markdown

internal sealed class MarkdownElement {
    data class Heading(val level: Int, val text: String) : MarkdownElement()
    data class Paragraph(val text: String) : MarkdownElement()
    data class Bold(val text: String) : MarkdownElement()
    data class ListItem(val text: String) : MarkdownElement()
}