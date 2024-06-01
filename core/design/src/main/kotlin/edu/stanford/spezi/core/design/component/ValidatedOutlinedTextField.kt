package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.TextStyles.labelSmall

@Composable
fun ValidatedOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit,
    labelText: String = "",
    errorText: String? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(labelText) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            isError = errorText != null,
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = labelSmall,
                color = Colors.error
            )
        }
    }
}


@Preview
@Composable
private fun ValidatedOutlinedTextFieldPreview(
    @PreviewParameter(ValidatedOutlinedTextFieldProvider::class) params: ValidatedOutlinedTextFieldParams
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


private data class ValidatedOutlinedTextFieldParams(
    val value: String,
    val labelText: String,
    val errorText: String?,
)