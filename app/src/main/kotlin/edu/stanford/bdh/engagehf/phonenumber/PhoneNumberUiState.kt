package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.ui.BottomSheetComposableContent
import edu.stanford.spezi.ui.Colors.onBackground
import edu.stanford.spezi.ui.Colors.primary
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

data class PhoneNumberUiState(
    val title: StringResource,
    val step: PhoneNumberStep,
    val actionButton: AsyncTextButton,
    override val onDismiss: () -> Unit,
) : BottomSheetComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier.padding(Spacings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = title.text(),
                    style = TextStyles.titleMedium,
                    color = onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_dialog_content_description),
                        tint = primary,
                        modifier = Modifier.size(Sizes.Icon.small)
                    )
                }
            }
            VerticalSpacer()

            step.Content()

            VerticalSpacer()

            actionButton.Content(modifier = Modifier.fillMaxWidth())
        }
    }

    @Composable
    private fun VerticalSpacer() {
        Spacer(modifier = Modifier.height(Spacings.large * 2))
    }
}

sealed interface PhoneNumberStep : ComposableContent

private class PhoneNumberUiStatePreviewParamProvider : PreviewParameterProvider<PhoneNumberUiState> {
    private val base = PhoneNumberUiState(
        title = StringResource(R.string.account_settings_add_phone_number),
        onDismiss = {},
        step = PhoneNumberInputUiModelPreviewParamProvider().values.first(),
        actionButton = AsyncTextButton(
            title = "Send Verification Message",
            action = {}
        ),
    )
    override val values: Sequence<PhoneNumberUiState>
        get() = sequenceOf(
            base,
            base.copy(step = VerificationCodeUiModelPreviewParamProvider().values.first()),
        )
}

@ThemePreviews
@Composable
fun PhoneNumberBottomSheetPreview(
    @PreviewParameter(PhoneNumberUiStatePreviewParamProvider::class) state: PhoneNumberUiState,
) {
    SpeziTheme { state.Content() }
}
