package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Spacings

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SignaturePad(
    uiState: ConsentUiState,
    onAction: (ConsentAction) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = uiState.firstName.value,
            onValueChange = {
                onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.FIRST_NAME))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("First Name") },
            singleLine = true,
            isError = uiState.firstName.error,
            trailingIcon = { Icon(Icons.Filled.Info, contentDescription = "Information Icon") }
        )
        Spacer(modifier = Modifier.height(Spacings.small))
        OutlinedTextField(
            value = uiState.lastName.value,
            onValueChange = {
                onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.LAST_NAME))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Last Name") },
            isError = uiState.lastName.error,
            singleLine = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Information Icon"
                )
            }
        )

        if (uiState.firstName.value.isNotBlank() && uiState.lastName.value.isNotBlank()) {
            Spacer(modifier = Modifier.height(Spacings.medium))
            Text("Signature:")
            SignatureCanvas(
                paths = uiState.paths.toMutableList(),
                firstName = uiState.firstName.value,
                lastName = uiState.lastName.value,
                onPathAdd = { path -> onAction(ConsentAction.AddPath(path)) }
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
                Button(
                    onClick = {
                        onAction(ConsentAction.Consent)
                    },
                    enabled = uiState.isValidForm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("I Consent")
                }
            }
        }
    }
}


@Preview
@Composable
private fun SignaturePadPreview(
    @PreviewParameter(SignaturePadPreviewProvider::class) data: SignaturePadPreviewData,
) {
    SignaturePad(
        uiState = ConsentUiState(
            firstName = FieldState(data.firstName),
            lastName = FieldState(data.lastName),
            paths = data.paths
        )
    ) {}
}

private data class SignaturePadPreviewData(
    val paths: MutableList<Path>,
    val firstName: String,
    val lastName: String,
)

private class SignaturePadPreviewProvider : PreviewParameterProvider<SignaturePadPreviewData> {
    override val values: Sequence<SignaturePadPreviewData> = sequenceOf(
        SignaturePadPreviewData(
            paths = mutableListOf(Path()),
            firstName = "",
            lastName = ""
        ),
        SignaturePadPreviewData(
            paths = mutableListOf(Path().apply { lineTo(100f, 100f) }.apply { lineTo(250f, 200f) }),
            firstName = "Jane",
            lastName = "Doe"
        )
    )
}
