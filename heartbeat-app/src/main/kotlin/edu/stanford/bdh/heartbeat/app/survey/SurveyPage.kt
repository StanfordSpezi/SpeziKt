package edu.stanford.bdh.heartbeat.app.survey

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
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
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.main.MainUiState
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

@Composable
private fun SurveyPage(
    uiState: SurveyUiState,
    onAction: (SurveyAction) -> Unit,
) {
    CustomDropdownInLazyColumn()
}

@ThemePreviews
@Composable
private fun SurveyLoadingFailed() {
    SpeziTheme(isPreview = true) {
        CustomDropdownInLazyColumn()
    }
}

@Composable
fun CustomDropdownInLazyColumn() {
    val options = List(20) { "Option ${it + 1}" }
    LazyColumn {
        item {
            CustomDropdownCard(options.first(), options)
        }
    }
}

@Composable
fun CustomDropdownCard(initialOption: String, options: List<String>) {
    var expanded by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf(initialOption) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium)
            .clickable { expanded = !expanded },
    ) {

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
                                }.padding(Spacings.medium),
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