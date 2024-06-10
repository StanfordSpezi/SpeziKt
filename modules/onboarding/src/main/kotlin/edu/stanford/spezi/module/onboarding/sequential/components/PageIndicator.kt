@file:Suppress("MagicNumber")
package edu.stanford.spezi.module.onboarding.sequential.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

/**
 * A page indicator that shows the current page and the total number of pages.
 * The indicator also has buttons to navigate to the next and previous pages.
 * @param currentPage The current page index
 * @param pageCount The total number of pages
 * @param textColor The color of the text and icons
 * @param onForward The action to perform when the forward button is clicked
 * @param onBack The action to perform when the back button is clicked
 * @param backgroundColor The background color of the indicator
 */
@Composable
fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    textColor: Color,
    onForward: () -> Unit,
    onBack: () -> Unit,
    backgroundColor: Color,
    actionText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                onBack()
            },
            content = {
                if (currentPage != 0) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Arrow Back",
                        tint = textColor
                    )
                }
                Text(
                    text = if (currentPage == 0) "Skip" else "Back",
                    color = textColor
                )
                if (currentPage == 0) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Forward",
                        tint = textColor
                    )
                }
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until pageCount) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (i == currentPage) 12.dp else 8.dp)
                        .background(
                            color = if (i == currentPage) textColor else textColor.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = { onForward() },
            content = {
                Text(
                    text = if (currentPage < pageCount - 1) "Forward" else actionText,
                    color = textColor
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = textColor
                )
            }
        )
    }
}

@Preview
@Composable
private fun PageIndicatorPreview(
    @PreviewParameter(PageIndicatorPreviewProvider::class) params: PageIndicatorPreviewParams,
) {
    PageIndicator(
        currentPage = params.currentPage,
        pageCount = params.pageCount,
        textColor = params.textColor,
        onForward = {},
        onBack = {},
        backgroundColor = params.backgroundColor,
        actionText = "Start"
    )
}

private class PageIndicatorPreviewProvider : PreviewParameterProvider<PageIndicatorPreviewParams> {
    override val values: Sequence<PageIndicatorPreviewParams> = sequenceOf(
        PageIndicatorPreviewParams(0, 5, Color.Black, Color.White),
        PageIndicatorPreviewParams(2, 5, Color.Black, Color.White),
        PageIndicatorPreviewParams(4, 5, Color.Black, Color.White)
    )
}

private data class PageIndicatorPreviewParams(
    val currentPage: Int,
    val pageCount: Int,
    val textColor: Color,
    val backgroundColor: Color,
)
