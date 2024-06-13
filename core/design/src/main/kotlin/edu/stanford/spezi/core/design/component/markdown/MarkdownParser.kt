package edu.stanford.spezi.core.design.component.markdown

import javax.inject.Inject

private const val HEADING_LEVEL_1 = 1
private const val HEADING_LEVEL_2 = 2
private const val HEADING_LEVEL_3 = 3

class MarkdownParser @Inject constructor() {

    fun parse(text: String): List<MarkdownElement> = buildList {
        text.lines().forEach { line ->
            when {
                line.startsWith("# ") -> add(
                    MarkdownElement.Heading(
                        HEADING_LEVEL_1,
                        line.removePrefix("# ").trim()
                    )
                )

                line.startsWith("## ") -> add(
                    MarkdownElement.Heading(
                        HEADING_LEVEL_2,
                        line.removePrefix("## ").trim()
                    )
                )

                line.startsWith("### ") -> add(
                    MarkdownElement.Heading(
                        HEADING_LEVEL_3,
                        line.removePrefix("### ").trim()
                    )
                )

                line.startsWith("- ") -> add(
                    MarkdownElement.ListItem(
                        line.removePrefix("- ").trim()
                    )
                )

                line.contains("**") -> add(MarkdownElement.Bold(line.replace("**", "").trim()))
                else -> add(MarkdownElement.Paragraph(line.trim()))
            }
        }
    }
}
