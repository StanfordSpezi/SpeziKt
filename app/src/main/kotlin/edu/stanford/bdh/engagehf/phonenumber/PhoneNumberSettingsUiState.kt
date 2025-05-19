package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.design.component.CommonScaffold
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class PhoneNumberSettingsUiState(
    val bottomSheet: AddPhoneNumberBottomSheet?,
    val phoneNumbers: List<PhoneNumberUiModel>,
    val onAddPhoneNumberClicked: () -> Unit,
    val onBackClicked: () -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        CommonScaffold(
            title = stringResource(R.string.phone_numbers_title),
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                    )
                }
            },
            actions = {
                IconButton(onClick = onAddPhoneNumberClicked) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        ) {
            LazyColumn(modifier = Modifier.padding(Spacings.medium)) {
                item {
                    Text(
                        modifier = Modifier.padding(bottom = Spacings.small),
                        text = stringResource(R.string.phone_number_verified_phone_numbers_header_title),
                        style = TextStyles.titleMedium,
                    )
                }
                if (phoneNumbers.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.phone_number_empty_list_message),
                            style = TextStyles.bodySmall,
                        )
                    }
                } else {
                    items(phoneNumbers) { phoneNumber ->
                        phoneNumber.Content()
                        HorizontalDivider()
                    }
                }
            }
            bottomSheet?.Sheet(Modifier)
        }
    }
}

class PhoneNumberSettingsUiStatePreviewParamProvider : PreviewParameterProvider<PhoneNumberSettingsUiState> {
    private val base = PhoneNumberSettingsUiState(
        bottomSheet = null,
        phoneNumbers = emptyList(),
        onBackClicked = {},
        onAddPhoneNumberClicked = {}
    )

    override val values: Sequence<PhoneNumberSettingsUiState>
        get() = sequenceOf(
            base,
            base.copy(
                phoneNumbers = listOf(
                    PhoneNumberUiModel(
                        phoneNumber = "+1 234 567 8900",
                        onDeleteClicked = {}
                    )
                )
            ),
        )
}

@ThemePreviews
@Composable
private fun Preview(
    @PreviewParameter(PhoneNumberSettingsUiStatePreviewParamProvider::class) model: PhoneNumberSettingsUiState,
) {
    SpeziTheme { model.Content() }
}
