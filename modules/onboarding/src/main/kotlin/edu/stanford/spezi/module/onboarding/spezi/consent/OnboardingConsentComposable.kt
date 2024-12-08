package edu.stanford.spezi.module.onboarding.spezi.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.component.StringResource.Companion.invoke
import edu.stanford.spezi.core.design.component.markdown.MarkdownElement
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.text.MarkdownBytes
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.module.onboarding.consent.ConsentAction
import edu.stanford.spezi.module.onboarding.consent.ConsentUiState
import edu.stanford.spezi.module.onboarding.consent.ConsentViewModel
import edu.stanford.spezi.module.onboarding.core.OnboardingComposable
import edu.stanford.spezi.module.onboarding.core.OnboardingTitle
import kotlinx.coroutines.launch

@Composable
fun OnboardingConsentComposable(
    markdown: suspend () -> ByteArray,
    action: suspend () -> Unit,
    title: String? = StringResource("Consent").text(),
    identifier: String = remember { "ConsentDocument" },
    exportConfiguration: ConsentDocumentExportConfiguration = remember { ConsentDocumentExportConfiguration() },
) {
    val viewModel: ConsentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    OnboardingConsentComposableContent(
        markdown = markdown,
        action = action,
        title = title,
        identifier = identifier,
        exportConfiguration = exportConfiguration,
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun OnboardingConsentComposableContent(
    markdown: suspend () -> ByteArray,
    action: suspend () -> Unit,
    title: String?,
    identifier: String,
    exportConfiguration: ConsentDocumentExportConfiguration,
    uiState: ConsentUiState,
    onAction: (ConsentAction) -> Unit,
) {
    val actionScope = rememberCoroutineScope()
    OnboardingComposable(
        modifier = Modifier
            .testIdentifier(OnboardingConsentTestIdentifier.ROOT)
            .fillMaxSize(),
        title = {
            title?.let {
                OnboardingTitle(it)
            }
        },
        content = {
            ConsentDocument(
                markdown = markdown,
                viewState = remember { mutableStateOf(ConsentViewState.Base(ViewState.Idle)) },
                exportConfiguration = exportConfiguration,
            ).Composable(
                modifier = Modifier.padding(bottom = Spacings.medium),
                uiState = uiState,
                onAction = onAction,
            )
        },
        action = {
            Button(
                onClick = {
                    actionScope.launch {
                        onAction(ConsentAction.Consent(identifier, exportConfiguration))
                        action()
                    }
                },
                enabled = uiState.isValidForm,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Text("I Consent")
            }
        }
    )
    Column {
        Spacer(modifier = Modifier.height(Spacings.medium))
        MarkdownBytes(markdown)
        Spacer(
            modifier = Modifier
                .height(Spacings.small)
                .weight(1f)
        )
    }
}

@ThemePreviews
@Composable
private fun OnboardingConsentPreview(
    @PreviewParameter(OnboardingConsentPreviewProvider::class) uiState: ConsentUiState,
) {
    SpeziTheme {
        OnboardingConsentComposableContent(
            markdown = { ByteArray(0) },
            action = {},
            title = null,
            identifier = "ConsentDocument",
            exportConfiguration = ConsentDocumentExportConfiguration(),
            uiState = uiState,
            onAction = {}
        )
    }
}

@Suppress("MagicNumber")
private class OnboardingConsentPreviewProvider : PreviewParameterProvider<ConsentUiState> {
    override val values: Sequence<ConsentUiState> = sequenceOf(
        ConsentUiState(
            name = PersonNameComponents(givenName = "John", familyName = "Doe"),
            paths = mutableListOf(Path().apply { lineTo(100f, 100f) }),
            markdownElements = listOf(
                MarkdownElement.Heading(1, "Consent"),
                MarkdownElement.Paragraph("Please sign below to indicate your consent."),
            ),
        ), ConsentUiState(
            name = PersonNameComponents(givenName = "", familyName = ""),
            paths = mutableListOf(),
        )
    )
}

enum class OnboardingConsentTestIdentifier {
    ROOT,
}