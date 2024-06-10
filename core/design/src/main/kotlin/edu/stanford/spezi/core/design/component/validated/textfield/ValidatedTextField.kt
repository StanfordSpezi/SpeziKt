@file:Suppress("FunctionName", "LongParameterList", "UnusedPrivateMember")
package edu.stanford.spezi.core.design.component.validated.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
fun ValidatedTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    labelText: String = "",
    errorText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = {
                Text(labelText)
            },
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            isError = errorText != null,
            supportingText = {
                if (errorText != null) {
                    Text(
                        text = errorText,
                    )
                }
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            readOnly = readOnly
        )
    }
}

@Preview
@Composable
private fun ValidatedTextFieldPreview(
    @PreviewParameter(ValidatedTextFieldProvider::class) params: ValidatedTextFieldParams,
) {
    ValidatedTextField(
        value = params.value,
        onValueChange = {},
        labelText = params.labelText,
        errorText = params.errorText,
        trailingIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Edit, contentDescription = "Select Date")
            }
        },
    )
}

private class ValidatedTextFieldProvider :
    PreviewParameterProvider<ValidatedTextFieldParams> {
    override val values = sequenceOf(data, data.copy(errorText = null))
}

private val data =
    ValidatedTextFieldParams(
        value = "",
        labelText = "Label",
        errorText = "The input is invalid",
    )

private data class ValidatedTextFieldParams(
    val value: String,
    val labelText: String,
    val errorText: String?,
)
