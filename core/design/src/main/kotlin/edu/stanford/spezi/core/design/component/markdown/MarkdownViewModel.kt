package edu.stanford.spezi.core.design.component.markdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

internal class MarkdownViewModel @AssistedInject internal constructor(
    @Assisted private val data: suspend () -> ByteArray,
    private val markdownParser: MarkdownParser,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MarkdownUiState())
    val uiState: StateFlow<MarkdownUiState> = _uiState

    init {
        viewModelScope.launch {
            val markdownText = data().toString(StandardCharsets.UTF_8)
            val markdownElements = markdownParser.parse(markdownText)
            _uiState.update {
                it.copy(elements = markdownElements)
            }
        }
    }
}
