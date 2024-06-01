package edu.stanford.spezi.core.design.component.markdown

internal fun parseMarkdown(markdownText: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    markdownText.lines().forEach { line ->
        when {
            line.startsWith("# ") -> elements.add(
                MarkdownElement.Heading(
                    1,
                    line.removePrefix("# ").trim()
                )
            )

            line.startsWith("## ") -> elements.add(
                MarkdownElement.Heading(
                    2,
                    line.removePrefix("## ").trim()
                )
            )

            line.startsWith("### ") -> elements.add(
                MarkdownElement.Heading(
                    3,
                    line.removePrefix("### ").trim()
                )
            )

            line.startsWith("- ") -> elements.add(
                MarkdownElement.ListItem(
                    line.removePrefix("- ").trim()
                )
            )

            line.contains("**") -> elements.add(MarkdownElement.Bold(line.replace("**", "").trim()))
            else -> elements.add(MarkdownElement.Paragraph(line.trim()))
        }
    }
    return elements
}