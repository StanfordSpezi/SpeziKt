package edu.stanford.spezi.module.onboarding.consent

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.model.ViewState
import java.nio.charset.StandardCharsets

data class ConsentDocument(
    private val markdown: suspend () -> ByteArray,
    private val viewState: MutableState<ConsentViewState>,
    private val givenNameTitle: StringResource = LocalizationDefaults.givenNameTitle,
    private val givenNamePlaceholder: StringResource = LocalizationDefaults.givenNamePlaceholder,
    private val familyNameTitle: StringResource = LocalizationDefaults.familyNameTitle,
    private val familyNamePlaceholder: StringResource = LocalizationDefaults.familyNamePlaceholder,
    private val exportConfiguration: ConsentDocumentExportConfiguration = ConsentDocumentExportConfiguration(),
) {
    object LocalizationDefaults {
        val givenNameTitle = StringResource("Given Name")
        val givenNamePlaceholder = StringResource("Given Name Placeholder")
        val familyNameTitle = StringResource("Family Name")
        val familyNamePlaceholder = StringResource("Family Name Placeholder")
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    internal fun Composable(
        modifier: Modifier = Modifier,
        uiState: ConsentUiState,
        onAction: (ConsentAction) -> Unit,
    ) {
        val givenName = uiState.name.givenName ?: ""
        val familyName = uiState.name.familyName ?: ""

        val keyboardController = LocalSoftwareKeyboardController.current
        Column(modifier = modifier) {
            OutlinedTextField(
                value = givenName,
                onValueChange = {
                    onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.FIRST_NAME))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(givenNameTitle.text()) },
                singleLine = true,
                placeholder = { Text(givenNamePlaceholder.text()) },
                trailingIcon = { Icon(Icons.Filled.Info, contentDescription = "Information Icon") }
            )
            Spacer(modifier = Modifier.height(Spacings.small))
            OutlinedTextField(
                value = familyName,
                onValueChange = {
                    onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.LAST_NAME))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(familyNameTitle.text()) },
                placeholder = { Text(familyNamePlaceholder.text()) },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Information Icon"
                    )
                }
            )

            if (givenName.isNotBlank() && familyName.isNotBlank()) {
                Spacer(modifier = Modifier.height(Spacings.medium))
                Text("Signature:")
                SignatureCanvas(
                    paths = uiState.paths.toMutableList(),
                    firstName = givenName,
                    lastName = familyName,
                    onPathAdd = { path ->
                        onAction(ConsentAction.AddPath(path))
                        keyboardController?.hide()
                    }
                )
                Spacer(modifier = Modifier.height(Spacings.medium))
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onClick = {
                            if (uiState.paths.isNotEmpty()) {
                                onAction(ConsentAction.Undo)
                            }
                        },
                        enabled = uiState.paths.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Undo")
                    }
                    Spacer(modifier = Modifier.width(Spacings.medium))
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun ConsentDocumentComposablePreview(
    @PreviewParameter(ConsentDocumentComposablePreviewProvider::class) data: ConsentDocumentComposablePreviewData,
) {
    ConsentDocument(
        markdown = { "".toByteArray(StandardCharsets.UTF_8) },
        viewState = remember { mutableStateOf(ConsentViewState.Base(ViewState.Idle)) },
    ).Composable(
        uiState = ConsentUiState(
            name = data.name,
            paths = data.paths
        )
    ) {}
}

private data class ConsentDocumentComposablePreviewData(
    val paths: MutableList<Path>,
    val name: PersonNameComponents,
)

private class ConsentDocumentComposablePreviewProvider : PreviewParameterProvider<ConsentDocumentComposablePreviewData> {
    override val values: Sequence<ConsentDocumentComposablePreviewData> = sequenceOf(
        ConsentDocumentComposablePreviewData(
            paths = mutableListOf(Path()),
            name = PersonNameComponents(givenName = "", familyName = "")
        ),
        @Suppress("MagicNumber")
        ConsentDocumentComposablePreviewData(
            paths = mutableListOf(Path().apply { lineTo(100f, 100f) }.apply { lineTo(250f, 200f) }),
            name = PersonNameComponents(givenName = "Jane", familyName = "Doe")

        )
    )
}
