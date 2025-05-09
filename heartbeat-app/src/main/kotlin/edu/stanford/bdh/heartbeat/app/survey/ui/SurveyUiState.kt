package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.modules.design.component.CommonScaffold
import edu.stanford.spezi.modules.design.component.Screen
import edu.stanford.spezi.ui.Spacings

data class SurveyUiState(
    val pageTitle: String,
    val questionState: SurveyQuestionState,
) : Screen {
    @Composable
    override fun Body(modifier: Modifier) {
        CommonScaffold(modifier = modifier, title = pageTitle) {
            Column(modifier = Modifier.padding(horizontal = Spacings.medium)) {
                questionState.Body(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
