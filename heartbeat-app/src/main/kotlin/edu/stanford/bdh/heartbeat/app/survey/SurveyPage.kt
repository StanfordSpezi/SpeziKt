package edu.stanford.bdh.heartbeat.app.survey

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.main.MainUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionTitle
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItem
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyProgress
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
    val title: QuestionTitle,
) : SurveyItem {
    @Composable
    override fun Content(modifier: Modifier) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium)
        ) {
            item { progress.Content(Modifier) }
            item { title.Content(Modifier) }
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
            .padding(Spacings.medium)
            .border(2.dp, Color.Black)
    ) {
        SurveyQuestionItem(
            progress = SurveyProgress(0.7f),
            title = QuestionTitle(content = uiState?.step?.question?.title1 ?: "")
        ).Content(Modifier.weight(1f))
    }
}

private @Composable
fun TestDropdown(modifier: Modifier = Modifier) {
    val options = List(20) { "Option ${it + 1}" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        item {
            CustomDropdownCard(options.first(), options)
        }
    }
}

@ThemePreviews
@Composable
private fun SurveyLoadingFailed() {
    SpeziTheme(isPreview = true) {
        SurveyPage(null, {})
    }
}

@Composable
fun CustomDropdownCard(initialOption: String, options: List<String>) {
    var expanded by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf(initialOption) }

    SurveyCard(modifier = Modifier.clickable { expanded = !expanded }) {

        Column {

            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth()
                    .padding(Spacings.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedOption, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")

            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = Spacings.medium))
            if (expanded) {
                Column {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOption = option
                                }
                                .padding(Spacings.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = option, modifier = Modifier.weight(1f))
                            if (selectedOption == option) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Dropdown")
                            }
                        }
                    }
                }
            }
        }
    }
}