package edu.stanford.spezikt.core.design.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezikt.core.design.theme.SpeziTheme

@Composable
fun SpeziValidatedOutlinedTextField(
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String,
    labelText: String,
    errorText: String?,
    isValid: Boolean
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
            isError = !isValid,
        )
        if (!isValid) {
            Text(
                text = errorText ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SpeziValidatedOutlinedTextFieldPreview(
    @PreviewParameter(SpeziValidatedOutlinedTextFieldProvider::class) params: SpeziValidatedOutlinedTextFieldParams
) {
    SpeziTheme {
        SpeziValidatedOutlinedTextField(
            onValueChange = {},
            value = params.value,
            labelText = params.labelText,
            errorText = params.errorText,
            isValid = params.isValid
        )
    }
}

class SpeziValidatedOutlinedTextFieldProvider :
    PreviewParameterProvider<SpeziValidatedOutlinedTextFieldParams> {
    override val values = sequenceOf(
        SpeziValidatedOutlinedTextFieldParams(
            value = "",
            labelText = "Label",
            errorText = "The input is invalid",
            isValid = false
        ),
        SpeziValidatedOutlinedTextFieldParams(
            value = "",
            labelText = "Label",
            errorText = "",
            isValid = true
        )
    )
}

data class SpeziValidatedOutlinedTextFieldParams(
    val value: String,
    val labelText: String,
    val errorText: String,
    val isValid: Boolean
)