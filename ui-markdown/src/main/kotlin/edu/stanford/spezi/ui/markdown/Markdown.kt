package edu.stanford.spezi.ui.markdown

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.markdown.internal.DEFAULT_MARKDOWN_PARSER
import edu.stanford.spezi.ui.markdown.internal.parseAnnotatedString
import org.intellij.markdown.parser.MarkdownParser
import java.nio.charset.StandardCharsets

@Composable
fun MarkdownBytes(
    bytes: ByteArray,
    parser: MarkdownParser = DEFAULT_MARKDOWN_PARSER,
    onFailure: ((Throwable) -> Unit)? = null,
) {
    MarkdownBytes(
        bytes = { bytes },
        parser = parser,
        onFailure = onFailure,
    )
}

@Composable
fun MarkdownBytes(
    bytes: suspend () -> ByteArray,
    parser: MarkdownParser = DEFAULT_MARKDOWN_PARSER,
    onFailure: ((Throwable) -> Unit)? = null,
) {
    MarkdownString(
        string = { bytes().toString(StandardCharsets.UTF_8) },
        parser = parser,
        onFailure = onFailure,
    )
}

@Composable
fun MarkdownString(
    string: String,
    parser: MarkdownParser = DEFAULT_MARKDOWN_PARSER,
    onFailure: ((Throwable) -> Unit)? = null,
) {
    MarkdownString(
        string = { string },
        parser = parser,
        onFailure = onFailure,
    )
}

@Composable
fun MarkdownString(
    string: suspend () -> String,
    parser: MarkdownParser = DEFAULT_MARKDOWN_PARSER,
    onFailure: ((Throwable) -> Unit)? = null,
) {
    Markdown(
        build = { parser.parseAnnotatedString(string()) },
        onFailure = onFailure,
    )
}

@Composable
fun Markdown(
    build: suspend () -> AnnotatedString,
    onFailure: ((Throwable) -> Unit)? = null,
) {
    var markdownContent by remember { mutableStateOf<AnnotatedString?>(null) }

    @Suppress("detekt:TooGenericExceptionCaught")
    LaunchedEffect(build) {
        try {
            markdownContent = build()
        } catch (throwable: Throwable) {
            onFailure?.invoke(throwable)
        }
    }

    markdownContent?.let {
        Text(it)
    } ?: CircularProgressIndicator()
}

@ThemePreviews
@Composable
private fun MarkdownPreview() {
    SpeziTheme(isPreview = true) {
        MarkdownString("""
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
