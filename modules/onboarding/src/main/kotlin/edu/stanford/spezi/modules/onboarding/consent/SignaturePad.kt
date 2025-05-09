@file:Suppress("MagicNumber")

package edu.stanford.spezi.modules.onboarding.consent

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.strokes.Stroke
import edu.stanford.spezi.modules.onboarding.R
import edu.stanford.spezi.ui.Spacings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SignaturePad(
    uiState: ConsentUiState,
    onAction: (ConsentAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()
    var currentStrokesView by remember { mutableStateOf<InProgressStrokesView?>(null) }

    Column(modifier = Modifier.imePadding()) {
        OutlinedTextField(
            value = uiState.firstName.value,
            onValueChange = {
                onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.FIRST_NAME))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.onboarding_first_name)) },
            singleLine = true,
            isError = uiState.firstName.error,
            trailingIcon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.onboarding_first_name)) }
        )
        Spacer(modifier = Modifier.height(Spacings.small))
        OutlinedTextField(
            value = uiState.lastName.value,
            onValueChange = {
                onAction(ConsentAction.TextFieldUpdate(it, TextFieldType.LAST_NAME))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.onboarding_last_name)) },
            isError = uiState.lastName.error,
            singleLine = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = stringResource(R.string.onboarding_last_name)
                )
            }
        )

        if (uiState.firstName.value.isNotBlank() && uiState.lastName.value.isNotBlank()) {
            Spacer(modifier = Modifier.height(Spacings.medium))
            Text(stringResource(R.string.onboarding_signature))

            SignatureCanvas(
                firstName = uiState.firstName.value,
                lastName = uiState.lastName.value,
                onViewCreated = {
                    onAction(ConsentAction.ClearPath)
                    currentStrokesView = it
                },
                onPathAdd = {
                    onAction(ConsentAction.AddPath(it))
                    // This is needed, otherwise having the keyboard out, will cause the signature to not be deletable
                    // most likely cause is, that hiding the keyboard interrupts the stroke, causing it to be malformed
                    scope.launch {
                        delay(100)
                        keyboardController?.hide()
                    }
                },
            )

            Spacer(modifier = Modifier.height(Spacings.medium))
            Row(modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(
                    onClick = {
                        if (uiState.paths.isNotEmpty()) {
                            currentStrokesView?.cancelStroke(uiState.paths.last().first)
                            currentStrokesView?.removeFinishedStrokes(setOf(uiState.paths.last().first))
                            onAction(ConsentAction.UndoPath)
                        }
                    },
                    enabled = uiState.paths.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.onboarding_undo))
                }
                Spacer(modifier = Modifier.width(Spacings.medium))
                Button(
                    onClick = {
                        onAction(ConsentAction.Consent)
                    },
                    enabled = uiState.isValidForm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.onboarding_i_consent))
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Preview
@Composable
private fun SignaturePadPreview(
    @PreviewParameter(SignaturePadPreviewProvider::class) data: SignaturePadPreviewData,
) {
    SignaturePad(
        uiState = ConsentUiState(
            firstName = FieldState(data.firstName),
            lastName = FieldState(data.lastName),
            paths = data.paths,
        ),
        onAction = {}
    )
}

private data class SignaturePadPreviewData(
    val paths: MutableList<Pair<InProgressStrokeId, Stroke>>,
    val firstName: String,
    val lastName: String,
)

private class SignaturePadPreviewProvider : PreviewParameterProvider<SignaturePadPreviewData> {
    override val values: Sequence<SignaturePadPreviewData> = sequenceOf(
        SignaturePadPreviewData(
            paths = mutableListOf(),
            firstName = "",
            lastName = "",
        ),
        SignaturePadPreviewData(
            paths = mutableListOf(),
            firstName = "Jane",
            lastName = "Doe"
        )
    )
}
