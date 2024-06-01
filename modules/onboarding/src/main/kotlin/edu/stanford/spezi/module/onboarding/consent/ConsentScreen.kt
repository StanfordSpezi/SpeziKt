package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.markdown.MarkdownComponent
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme

@Composable
fun ConsentScreen() {
    val viewModel: ConsentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    ConsentScreen(
        onAction = viewModel::onAction, uiState = uiState
    )
}

@Composable
private fun ConsentScreen(
    uiState: ConsentUiState,
    onAction: (ConsentAction) -> Unit,
) {
    Column(modifier = Modifier.padding(Spacings.medium)) {
        Spacer(modifier = Modifier.height(Spacings.medium))
        MarkdownComponent(
            markdownText = """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
        """.trimIndent()
        )
        SignaturePad(
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Preview
@Composable
private fun ConsentScreenPreview(
    @PreviewParameter(ConsentScreenPreviewProvider::class) uiState: ConsentUiState
) {
    SpeziTheme {
        ConsentScreen(uiState = uiState, onAction = { })
    }
}

private class ConsentScreenPreviewProvider : PreviewParameterProvider<ConsentUiState> {
    override val values: Sequence<ConsentUiState> = sequenceOf(
        ConsentUiState(
            firstName = FieldState("John"),
            lastName = FieldState("Doe"),
            paths = mutableListOf(Path().apply { lineTo(100f, 100f) }),
            isValidForm = true
        ), ConsentUiState(
            firstName = FieldState(""),
            lastName = FieldState(""),
            paths = mutableListOf(),
            isValidForm = false
        )
    )
}