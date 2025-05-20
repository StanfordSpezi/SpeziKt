package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class VerificationCodeUiModel(
    val description: StringResource,
    val phoneNumber: String,
    val digits: List<Int?>,
    val focusedIndex: Int,
    val onValueChanged: (index: Int, newDigit: String) -> Unit,
) : PhoneNumberStep {

    @Composable
    override fun Content(modifier: Modifier) {
        val focusRequesters = remember(digits.size) { List(digits.size) { FocusRequester() } }
        LaunchedEffect(focusedIndex) {
            focusRequesters.getOrNull(focusedIndex)?.requestFocus()
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium)
        ) {
            Text(
                modifier = modifier.fillMaxWidth().padding(horizontal = Spacings.large),
                text = description.text(),
                style = TextStyles.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                digits.forEachIndexed { index, digit ->
                    NumberTextField(
                        value = digit?.toString().orEmpty(),
                        onValueChanged = { newDigit ->
                            onValueChanged(index, newDigit)
                        },
                        focusRequester = focusRequesters[index]
                    )
                }
            }
        }
    }

    @Composable
    private fun NumberTextField(
        value: String,
        onValueChanged: (String) -> Unit,
        focusRequester: FocusRequester,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            singleLine = true,
            textStyle = TextStyles.titleLarge.copy(textAlign = TextAlign.Center, color = Colors.onSurface),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.size(52.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp),
                )
                .focusRequester(focusRequester),
            decorationBox = @Composable { innerTextField ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    innerTextField()
                }
            },
        )
    }
}

class VerificationCodeUiModelPreviewParamProvider : PreviewParameterProvider<VerificationCodeUiModel> {
    override val values: Sequence<VerificationCodeUiModel>
        get() = sequenceOf(
            VerificationCodeUiModel(
                description = StringResource(R.string.enter_verification_code_description),
                phoneNumber = "",
                digits = listOf(1, 2, null, null, null, null),
                focusedIndex = 0,
                onValueChanged = { _, _ -> },
            )
        )
}

@ThemePreviews
@Composable
private fun Preview(
    @PreviewParameter(VerificationCodeUiModelPreviewParamProvider::class) item: VerificationCodeUiModel,
) {
    SpeziTheme {
        item.Content(modifier = Modifier.fillMaxWidth())
    }
}
