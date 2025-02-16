package edu.stanford.bdh.heartbeat.app.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.main.MainUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItem
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyProgress
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionTitle
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.ChoicesFieldItemPreviewParameterProvider
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.FormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.datePickerFormField
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.textFieldArea
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.textFieldItem
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews


@Composable
fun SurveyPage(
    onboardingState: MainUiState.Authenticated.Survey.Content,
) {
    val viewModel = hiltViewModel<SurveyViewModel, SurveyViewModel.Factory>(
        creationCallback = { factory -> factory.create(onboardingState) },
        key = onboardingState.onboarding.displayStatus.surveyToken
    )
    val uiState by viewModel.uiState.collectAsState()
    SurveyPage(uiState, viewModel::onAction)
}


data class SurveyQuestionItem(
    val progress: SurveyProgress,
    val title: SurveyQuestionTitle,
    val fields: List<FormFieldItem>
) : SurveyItem {

    @Composable
    override fun Content(modifier: Modifier) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium)
        ) {
            item { progress.Content(Modifier.padding(top = Spacings.medium)) }
            item { title.Content(Modifier) }
            itemsIndexed(fields) { index, field ->
                field.Content(Modifier.padding(bottom = if (index == fields.size - 1) Spacings.medium else 0.dp))
            }
        }
    }
}

@Composable
private fun SurveyPage(
    uiState: SurveyUiState2?,
    onAction: (SurveyAction) -> Unit,
) {
    val options = List(20) { "Option ${it + 1}" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacings.medium)
    ) {
        SurveyQuestionItem(
            progress = SurveyProgress(0.7f),
            title = SurveyQuestionTitle(content = uiState?.step?.question?.title1 ?: ""),
            fields = ChoicesFieldItemPreviewParameterProvider().values.toList() + datePickerFormField + textFieldArea + textFieldItem,

        ).Content(Modifier.weight(1f))

        // LoadingSurveyItem.Content(Modifier)
    }
}


@ThemePreviews
@Composable
private fun SurveyLoadingFailed() {
    SpeziTheme(isPreview = true) {
        SurveyPage(null, {})
    }
}
