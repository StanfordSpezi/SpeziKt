package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.disabledAlpha
import edu.stanford.spezi.ui.lighten
import edu.stanford.spezi.ui.noRippleClickable
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Colors.primary
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles.bodyMedium
import edu.stanford.spezi.ui.theme.TextStyles.bodySmall
import edu.stanford.spezi.ui.theme.ThemePreviews

data class PhoneNumberInputUiModel(
    val phoneNumber: String,
    val onPhoneNumberChanged: (String) -> Unit,
    val errorMessage: StringResource?,
    val countryCodeButtonTitle: String,
    val onCountryCodeButtonClicked: () -> Unit,
    val countrySelection: CountryCodeSelectionUiModel?,
) : PhoneNumberStep {
    @Composable
    override fun Content(modifier: Modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Colors.surface.lighten()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CountryCodeButton()

            BasicTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChanged,
                singleLine = false,
                textStyle = LocalTextStyle.current.copy(
                    color = Colors.secondary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .height(52.dp)
                    .fillMaxWidth(),
                decorationBox = @Composable { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacings.small),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (phoneNumber.isEmpty()) {
                            Text(
                                modifier = Modifier.disabledAlpha(),
                                text = stringResource(R.string.phone_number_enter_placeholder_description),
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Text(
            modifier = Modifier
                .alpha(if (errorMessage != null) 1f else 0f)
                .padding(vertical = Spacings.small)
                .fillMaxWidth(),
            text = errorMessage?.text().orEmpty(),
            style = bodySmall,
            color = Colors.error,
        )
        countrySelection?.Sheet(Modifier.fillMaxSize())
    }

    @Composable
    private fun CountryCodeButton() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .noRippleClickable(onClick = onCountryCodeButtonClicked)
                .padding(start = Spacings.small)
        ) {
            Text(
                text = countryCodeButtonTitle,
                style = bodyMedium,
                color = primary
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(Sizes.Icon.small)
            )
        }
    }
}

class PhoneNumberInputUiModelPreviewParamProvider : PreviewParameterProvider<PhoneNumberInputUiModel> {
    override val values: Sequence<PhoneNumberInputUiModel>
        get() = sequenceOf(
            PhoneNumberInputUiModel(
                phoneNumber = "",
                onPhoneNumberChanged = {},
                errorMessage = StringResource(R.string.invalid_phone_number_message),
                countryCodeButtonTitle = "\uD83C\uDDE9\uD83C\uDDEA +49",
                onCountryCodeButtonClicked = {},
                countrySelection = null,
            )
        )
}

@ThemePreviews
@Composable
private fun Preview(
    @PreviewParameter(PhoneNumberInputUiModelPreviewParamProvider::class) model: PhoneNumberInputUiModel,
) {
    SpeziTheme {
        model.Content(modifier = Modifier.fillMaxWidth())
    }
}
