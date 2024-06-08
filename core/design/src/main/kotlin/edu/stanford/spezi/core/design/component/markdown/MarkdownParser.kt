package edu.stanford.spezi.core.design.component.markdown

const val HEADING_LEVEL_1 = 1
const val HEADING_LEVEL_2 = 2
const val HEADING_LEVEL_3 = 3
fun parseMarkdown(markdownText: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    markdownText.lines().forEach { line ->
        when {
            line.startsWith("# ") -> elements.add(
                MarkdownElement.Heading(
                    HEADING_LEVEL_1,
                    line.removePrefix("# ").trim()
                )
            )

            line.startsWith("## ") -> elements.add(
                MarkdownElement.Heading(
                    HEADING_LEVEL_2,
                    line.removePrefix("## ").trim()
                )
            )

            line.startsWith("### ") -> elements.add(
                MarkdownElement.Heading(
                    HEADING_LEVEL_3,
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
