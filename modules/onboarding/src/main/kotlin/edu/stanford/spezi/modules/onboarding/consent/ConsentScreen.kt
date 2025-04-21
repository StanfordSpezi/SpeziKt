package edu.stanford.spezi.modules.onboarding.consent

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.markdown.MarkdownComponent
import edu.stanford.spezi.ui.markdown.MarkdownElement
import edu.stanford.spezi.ui.testIdentifier

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
    Column(
        modifier = Modifier
            .testIdentifier(ConsentScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        Spacer(modifier = Modifier.height(Spacings.medium))
        MarkdownComponent(markdownElements = uiState.markdownElements)
        Spacer(
            modifier = Modifier
                .height(Spacings.small)
                .weight(1f)
        )
        SignaturePad(
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ConsentScreenPreview(
    @PreviewParameter(ConsentScreenPreviewProvider::class) uiState: ConsentUiState,
) {
    SpeziTheme {
        ConsentScreen(uiState = uiState, onAction = { })
    }
}

@Suppress("MagicNumber")
private class ConsentScreenPreviewProvider : PreviewParameterProvider<ConsentUiState> {
    override val values: Sequence<ConsentUiState> = sequenceOf(
        ConsentUiState(
            firstName = FieldState("John"),
            lastName = FieldState("Doe"),
            paths = mutableListOf(Path().apply { lineTo(100f, 100f) }),
            markdownElements = listOf(
                MarkdownElement.Heading(1, "Consent"),
                MarkdownElement.Paragraph("Please sign below to indicate your consent."),
            ),
        ), ConsentUiState(
            firstName = FieldState(""),
            lastName = FieldState(""),
            paths = mutableListOf(),
        )
    )
}

enum class ConsentScreenTestIdentifier {
    ROOT,
}
