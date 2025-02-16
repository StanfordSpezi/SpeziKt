package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.RectangleShimmerEffect
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.component.height
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

private const val PLACEHOLDERS_COUNT = 10

object LoadingSurveyItem : SurveyItem {

    @Composable
    override fun Content(modifier: Modifier) {
        LazyColumn {
            items(PLACEHOLDERS_COUNT) {
                SurveyCard(
                    modifier = Modifier.padding(
                        top = if (it == 0) Spacings.medium else Spacings.small,
                        bottom = if (it == PLACEHOLDERS_COUNT - 1) Spacings.medium else Spacings.small,
                    )
                ) {
                    RectangleShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(textStyle = TextStyles.titleLarge)
                    )

                    VerticalSpacer(height = Spacings.medium)

                    RectangleShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(textStyle = TextStyles.titleLarge)
                    )

                    VerticalSpacer(height = Spacings.medium)

                    RectangleShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.4f)
                            .height(textStyle = TextStyles.titleSmall)
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Previews() {
    SurveyItemPreview {
        LoadingSurveyItem.Content(Modifier)
    }
}
