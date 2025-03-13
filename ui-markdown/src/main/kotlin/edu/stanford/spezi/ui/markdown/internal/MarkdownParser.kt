package edu.stanford.spezi.ui.markdown.internal

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import edu.stanford.spezi.core.logging.SpeziLogger
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.CompositeASTNode
import org.intellij.markdown.ast.LeafASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.parser.MarkdownParser

// adapted from: https://github.com/volo-droid/Markdown-to-AnnotatedString/blob/main/library/src/main/java/dev/volo/markdown/annotatedstring/AnnotatedStringGenerator.kt

internal data class MarkdownStyle(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
)

internal fun MarkdownParser.parseAnnotatedString(text: String, style: MarkdownStyle): AnnotatedString {
    val tree = this.buildMarkdownTreeFromString(text)
    SpeziLogger.i { tree.toString() }
    return AnnotatedString.Builder(capacity = text.length)
        .appendMarkdown(text, tree, style)
        .toAnnotatedString()
}

@Suppress("detekt:CyclomaticComplexMethod", "detekt:LongMethod")
private fun AnnotatedString.Builder.appendMarkdown(
    text: String,
    node: ASTNode,
    style: MarkdownStyle,
): AnnotatedString.Builder {
    node.children.forEach { child ->
        when (child.type) {
            MarkdownElementTypes.PARAGRAPH -> {
                appendMarkdown(text, child, style)
            }
            MarkdownTokenTypes.EMPH, MarkdownElementTypes.EMPH -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.STRONG -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.CODE_SPAN -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                appendMarkdown(text, child, style)
            }
            MarkdownTokenTypes.TEXT -> append(child.getTextInNode(text).toString())
            MarkdownTokenTypes.BACKTICK -> append('`')
            MarkdownTokenTypes.COLON -> append(':')
            MarkdownTokenTypes.DOUBLE_QUOTE -> append('"')
            MarkdownTokenTypes.EOL -> append('\n')
            MarkdownTokenTypes.ESCAPED_BACKTICKS -> append('`')
            MarkdownTokenTypes.EXCLAMATION_MARK -> append('!')
            MarkdownTokenTypes.GT -> append('>')
            MarkdownTokenTypes.HARD_LINE_BREAK -> append("\n\n")
            MarkdownTokenTypes.LBRACKET -> append('[')
            MarkdownTokenTypes.LPAREN -> append('(')
            MarkdownTokenTypes.LT -> append('<')
            MarkdownTokenTypes.RBRACKET -> append(']')
            MarkdownTokenTypes.RPAREN -> append(')')
            MarkdownTokenTypes.SINGLE_QUOTE -> append('\'')
            MarkdownTokenTypes.WHITE_SPACE -> repeat(child.charCount) { append(' ') }
            // TODO: Add support for numbered lists
            MarkdownTokenTypes.LIST_BULLET, MarkdownTokenTypes.LIST_NUMBER -> append("\t\u2022\t")
            MarkdownTokenTypes.ATX_HEADER -> {
                appendMarkdown(text, child, style)
            }
            MarkdownTokenTypes.ATX_CONTENT -> {
                appendTrimmedMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_1 -> withStyle(style.h1) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_2 -> withStyle(style.h2) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_3 -> withStyle(style.h3) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_4 -> withStyle(style.h4) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_5 -> withStyle(style.h5) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ATX_6 -> withStyle(style.h6) {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.ORDERED_LIST -> {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.UNORDERED_LIST -> {
                appendMarkdown(text, child, style)
            }
            MarkdownElementTypes.LIST_ITEM -> {
                appendTrimmedMarkdown(text, child, style)
            }
            else -> {
                if (child is LeafASTNode) {
                    append(child.getTextInNode(text).toString())
                } else if (child is CompositeASTNode) {
                    appendMarkdown(text, child, style)
                }
                SpeziLogger.w { "Unexpected markdown node encountered. Skipping... ${child.type}" }
            }
        }
    }
    return this
}

private fun AnnotatedString.Builder.appendTrimmedMarkdown(
    text: String,
    node: ASTNode,
    style: MarkdownStyle,
) {
    append(
        AnnotatedString.Builder(capacity = node.charCount)
            .appendMarkdown(text, node, style)
            .toAnnotatedString()
            .trim()
    )
}

fun AnnotatedString.trim(): AnnotatedString {
    val originalText = this.text
    val startIndex = originalText.indexOfFirst { !it.isWhitespace() }
    val endIndex = originalText.indexOfLast { !it.isWhitespace() }

    // If the string is entirely whitespace, return an empty AnnotatedString
    if (startIndex == -1 || endIndex == -1) return AnnotatedString("")

    return subSequence(startIndex, endIndex + 1)
}

private fun <R : Any>AnnotatedString.Builder.withStyle(textStyle: TextStyle, block: AnnotatedString.Builder.() -> R) =
    withStyle(textStyle.toParagraphStyle()) {
        withStyle(textStyle.toSpanStyle()) {
            block()
        }
    }

private val ASTNode.charCount
    get() = endOffset - startOffset
