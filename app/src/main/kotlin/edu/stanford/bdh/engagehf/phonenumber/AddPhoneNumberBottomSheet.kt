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
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors.onBackground
import edu.stanford.spezi.ui.theme.Colors.primary
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class AddPhoneNumberBottomSheet(
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
                contentAlignment = Alignment.TopStart,
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

private class AddPhoneNumberBottomSheetParamProvider : PreviewParameterProvider<AddPhoneNumberBottomSheet> {
    private val base = AddPhoneNumberBottomSheet(
        title = StringResource(R.string.phone_number_add),
        onDismiss = {},
        step = PhoneNumberInputUiModelPreviewParamProvider().values.first(),
        actionButton = AsyncTextButton(
            title = "Send Verification Message",
            action = {}
        ),
    )
    override val values: Sequence<AddPhoneNumberBottomSheet>
        get() = sequenceOf(
            base,
            base.copy(step = VerificationCodeUiModelPreviewParamProvider().values.first()),
        )
}

@ThemePreviews
@Composable
fun AddPhoneNumberBottomSheetPreview(
    @PreviewParameter(AddPhoneNumberBottomSheetParamProvider::class) state: AddPhoneNumberBottomSheet,
) {
    SpeziTheme { state.Content() }
}
