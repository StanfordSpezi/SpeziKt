package edu.stanford.spezi.ui.markdown

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.ViewState
import edu.stanford.spezi.ui.markdown.internal.parseAnnotatedString
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.nio.charset.StandardCharsets

@Composable
fun MarkdownBytes(
    bytes: ByteArray,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    MarkdownBytes(
        bytes = { bytes },
        state = state,
    )
}

@Composable
fun MarkdownBytes(
    bytes: suspend () -> ByteArray,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    MarkdownString(
        string = { bytes().toString(StandardCharsets.UTF_8) },
        state = state,
    )
}

@Composable
fun MarkdownString(
    string: String,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    MarkdownString(
        string = { string },
        state = state,
    )
}

@Composable
fun MarkdownString(
    string: suspend () -> String,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    Markdown(
        build = { MarkdownParser(GFMFlavourDescriptor()).parseAnnotatedString(string()) },
        state = state,
    )
}

@Composable
fun Markdown(
    build: suspend () -> AnnotatedString,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    var markdownContent by remember { mutableStateOf<AnnotatedString?>(null) }

    @Suppress("detekt:TooGenericExceptionCaught")
    LaunchedEffect(Unit) {
        state.value = ViewState.Processing
        try {
            markdownContent = build()
            state.value = ViewState.Idle
        } catch (throwable: Throwable) {
            state.value = ViewState.Error(throwable)
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
