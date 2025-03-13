package edu.stanford.spezi.ui.markdown.internal

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

// adapted from: https://github.com/volo-droid/Markdown-to-AnnotatedString/blob/main/library/src/main/java/dev/volo/markdown/annotatedstring/AnnotatedStringGenerator.kt

internal val DEFAULT_MARKDOWN_PARSER get() = MarkdownParser(GFMFlavourDescriptor())

internal fun MarkdownParser.parseAnnotatedString(text: String): AnnotatedString {
    val tree = this.buildMarkdownTreeFromString(text)
    return AnnotatedString.Builder(capacity = text.length)
        .appendMarkdown(text, tree)
        .toAnnotatedString()
}

@Suppress("detekt:CyclomaticComplexMethod")
private fun AnnotatedString.Builder.appendMarkdown(text: String, node: ASTNode): AnnotatedString.Builder {
    node.children.forEach { child ->
        when (child.type) {
            MarkdownElementTypes.PARAGRAPH ->
                appendMarkdown(text, child)
            MarkdownElementTypes.EMPH -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendMarkdown(text, child)
            }
            MarkdownElementTypes.STRONG -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                appendMarkdown(text, child)
            }
            MarkdownElementTypes.CODE_SPAN -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                appendMarkdown(text, child)
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
        }
    }
    return this
}

private val ASTNode.charCount
    get() = endOffset - startOffset
