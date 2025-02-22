package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.FormFieldItem
import edu.stanford.spezi.core.design.component.RectangleShimmerEffect
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.component.height
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

interface SurveyQuestionState : SurveyItem {

    object Loading : SurveyQuestionState {

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

    data class Question(
        val progress: SurveyProgress,
        val title: SurveyQuestionTitle,
        val secondaryTitle: SurveyQuestionTitle?,
        val fields: List<FormFieldItem>,
        val backButton: QuestionButton?,
        val continueButton: QuestionButton,
        val onDisplayed: () -> Unit,
    ) : SurveyQuestionState {

        @Composable
        override fun Content(modifier: Modifier) {
            LaunchedEffect(title) { onDisplayed() }
            Column(modifier = modifier) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacings.medium)
                ) {
                    item { progress.Content(Modifier.padding(top = Spacings.medium)) }
                    item { title.Content(Modifier) }
                    secondaryTitle?.let { item { it.Content(Modifier) } }
                    items(fields) { field -> field.Content(Modifier) }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacings.medium),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.medium)
                ) {
                    backButton?.Content(Modifier.weight(1f))
                    continueButton.Content(Modifier.weight(1f))
                }
            }
        }
    }
}

private const val PLACEHOLDERS_COUNT = 10

@ThemePreviews
@Composable
private fun Previews() {
    SurveyItemPreview {
        SurveyQuestionState.Loading.Content(Modifier)
    }
}
