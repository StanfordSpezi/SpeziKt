package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.summary.QRCodeImageBitmapGenerator
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Colors.onBackground
import edu.stanford.spezi.ui.Colors.primary
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.DialogComposableContent
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

data class ShareHealthSummaryDialogUiState(
    val qrCodeBitmap: ImageBitmap,
    val oneTimeCode: String,
    val expiresIn: StringResource,
    val onViewHealthSummaryClicked: () -> Unit,
    val isViewHealthSummaryLoading: Boolean,
    override val onDismiss: () -> Unit,
) : DialogComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = stringResource(R.string.health_summary),
                    style = TextStyles.titleMedium,
                    color = onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    enabled = isViewHealthSummaryLoading.not(),
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

            Text(
                text = stringResource(R.string.qr_code_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacings.medium),
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier.padding(top = Spacings.medium),
                text = expiresIn.text(),
                color = Colors.secondary,
                style = TextStyles.bodyMedium,
            )

            Image(
                bitmap = qrCodeBitmap,
                contentDescription = stringResource(R.string.qr_code_description),
                modifier = modifier,
            )

            DefaultElevatedCard {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Spacings.medium))
                        .padding(Spacings.medium)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.qr_code_one_time_code_label),
                        style = TextStyles.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = oneTimeCode)
                }
            }

            HorizontalDivider()
            Text(
                text = stringResource(R.string.qr_code_summary_button_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacings.medium),
                textAlign = TextAlign.Center,
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncTextButton(
                    modifier = Modifier.align(Alignment.Center),
                    isLoading = isViewHealthSummaryLoading,
                    text = stringResource(R.string.qr_code_health_summary_button_title),
                    onClick = onViewHealthSummaryClicked,
                )
            }
        }
    }
}

@Suppress("MagicNumber")
private class ShareHealthSummaryUiStatePreviewParamProvider : PreviewParameterProvider<ShareHealthSummaryDialogUiState> {
    private val base = ShareHealthSummaryDialogUiState(
        qrCodeBitmap = QRCodeImageBitmapGenerator().generate("edu.stanford.bdh.engagehf", 600)!!,
        oneTimeCode = "41S8MJTN",
        expiresIn = StringResource(R.string.qr_code_expires_in, "4:55"),
        onViewHealthSummaryClicked = {},
        isViewHealthSummaryLoading = false,
        onDismiss = {},
    )
    override val values = sequenceOf(
        base,
        base.copy(isViewHealthSummaryLoading = true),
    )
}

@ThemePreviews
@Composable
private fun Preview(
    @PreviewParameter(ShareHealthSummaryUiStatePreviewParamProvider::class) state: ShareHealthSummaryDialogUiState,
) {
    SpeziTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            state.DialogContent()
        }
    }
}
