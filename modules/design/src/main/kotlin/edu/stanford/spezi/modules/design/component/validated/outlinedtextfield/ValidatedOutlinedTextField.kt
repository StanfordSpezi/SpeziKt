package edu.stanford.spezi.modules.design.component.validated.outlinedtextfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.ui.ComposableBlock

@Composable
fun ValidatedOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    labelText: String = "",
    errorText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    trailingIcon: ComposableBlock? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(labelText) },
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            isError = errorText != null,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            supportingText = errorText?.let {
                { Text(text = it) }
            },
        )
    }
}

@Preview
@Composable
private fun ValidatedOutlinedTextFieldPreview(
    @PreviewParameter(ValidatedOutlinedTextFieldProvider::class) params: ValidatedOutlinedTextFieldParams,
) {
    ValidatedOutlinedTextField(
        onValueChange = {},
        value = params.value,
        labelText = params.labelText,
        errorText = params.errorText,
    )
}

private class ValidatedOutlinedTextFieldProvider :
    PreviewParameterProvider<ValidatedOutlinedTextFieldParams> {
    override val values = sequenceOf(data, data.copy(errorText = null))
}

private val data =
    ValidatedOutlinedTextFieldParams(
        value = "",
        labelText = "Label",
        errorText = "The input is invalid",
    )
