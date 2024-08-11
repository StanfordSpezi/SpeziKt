package edu.stanford.bdh.engagehf.questionnaire

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.fhir.datacapture.QuestionnaireFragment
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.SpeziTheme
import org.hl7.fhir.r4.model.QuestionnaireResponse

@Composable
fun QuestionnaireScreen() {
    val viewModel = hiltViewModel<QuestionnaireViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    QuestionnaireScreen(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
fun QuestionnaireScreen(
    uiState: QuestionnaireViewModel.State,
    onAction: (QuestionnaireViewModel.Action) -> Unit,
) {
    when (uiState) {
        is QuestionnaireViewModel.State.Loading -> {
            CircularProgressIndicator()
        }

        is QuestionnaireViewModel.State.Error -> {
            Text(text = uiState.message)
        }

        is QuestionnaireViewModel.State.QuestionnaireLoaded -> {
            QuestionnaireLoaded(uiState, onAction)
        }
    }
}

@Composable
private fun QuestionnaireLoaded(
    uiState: QuestionnaireViewModel.State.QuestionnaireLoaded,
    onAction: (QuestionnaireViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        VerticalSpacer()
        Text(
            text = "Questionnaire",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        VerticalSpacer()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val fragmentState = rememberFragmentState()
            var questionnaireResponse by remember { mutableStateOf<QuestionnaireResponse?>(null) }
            AndroidFragment<QuestionnaireFragment>(
                fragmentState = fragmentState,
                modifier = Modifier
                    .fillMaxSize(),
                arguments = uiState.args
            ) { fragment ->
                fragment.setFragmentResultListener(
                    QuestionnaireFragment.SUBMIT_REQUEST_KEY
                ) { _, _ ->
                    onAction(
                        QuestionnaireViewModel.Action.SaveQuestionnaireResponse(
                            fragment.getQuestionnaireResponse()
                        )
                    )
                }
                fragment.setFragmentResultListener(
                    QuestionnaireFragment.CANCEL_REQUEST_KEY
                ) { _, _ ->
                    onAction(
                        QuestionnaireViewModel.Action.Cancel
                    )
                }
                fragment.getQuestionnaireResponse().let {
                    questionnaireResponse = it
                    questionnaireResponse?.status?.let { status ->
                        if (status == QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED) {
                            onAction(
                                QuestionnaireViewModel.Action.SaveQuestionnaireResponse(
                                    it
                                )
                            )
                        }
                    }
                    onAction(
                        QuestionnaireViewModel.Action.SaveQuestionnaireResponse(
                            it
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuestionnaireScreen() {
    SpeziTheme {
        QuestionnaireScreen(
            uiState = QuestionnaireViewModel.State.Loading,
            onAction = {}
        )
    }
}
