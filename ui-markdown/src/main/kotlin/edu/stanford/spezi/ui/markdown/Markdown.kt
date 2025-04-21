package edu.stanford.spezi.ui.markdown

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.ViewState
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
        build = { MarkdownParser().parse(string()) },
        state = state,
    )
}

@Composable
fun Markdown(
    build: suspend () -> List<MarkdownElement>,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
) {
    var markdownContent by remember { mutableStateOf<List<MarkdownElement>?>(null) }

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
        MarkdownComponent(it)
    } ?: CircularProgressIndicator()
}

@ThemePreviews
@Composable
private fun MarkdownPreview() {
    SpeziTheme {
        MarkdownString("This is a markdown **example**!")
    }
}
