package edu.stanford.spezi.questionnaire

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.testing.testIdentifier
import org.hl7.fhir.r4.model.Questionnaire

@Composable
fun QuestionnaireComposable(
    questionnaire: Questionnaire,
    modifier: Modifier = Modifier,
    completionStepMessage: String? = null,
    cancelBehavior: CancelBehavior = CancelBehavior.ShouldConfirmCancel,
    onResult: (QuestionnaireResult) -> Unit,
) {
    val completionItem = remember(completionStepMessage) {
        completionStepMessage?.let {
            Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "completion-step"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                text = completionStepMessage
            }
        }
    }
    val fullQuestionnaire = remember(questionnaire, completionItem) {
        completionItem?.let {
            questionnaire.copy().apply { addItem(it) }
        } ?: questionnaire
    }
    val questionnaireJson = remember(fullQuestionnaire) {
        runCatching {
            FhirContext.forR4()
                .newJsonParser()
                .encodeResourceToString(fullQuestionnaire)
        }
    }
    LaunchedEffect(questionnaireJson) {
        if (questionnaireJson.isFailure) {
            onResult(QuestionnaireResult.Failed)
        }
    }

    questionnaireJson.onSuccess {
        QuestionnaireComposable(
            questionnaireJson = it,
            modifier = modifier,
            cancelBehavior = cancelBehavior,
            onResult = onResult
        )
    }
}

@Composable
fun QuestionnaireComposable(
    questionnaireJson: String,
    modifier: Modifier = Modifier,
    cancelBehavior: CancelBehavior = CancelBehavior.ShouldConfirmCancel,
    onResult: (QuestionnaireResult) -> Unit,
) {
    val showsCancelAlert = remember { mutableStateOf(false) }
    if (showsCancelAlert.value) {
        QuestionnaireCancelAlert {
            onResult(QuestionnaireResult.Cancelled)
        }
    }

    val arguments = remember(questionnaireJson, cancelBehavior) {
        bundleOf(
            "questionnaire" to questionnaireJson,
            "show-cancel-button" to (cancelBehavior != CancelBehavior.Disabled),
        )
    }
    val fragmentState = rememberFragmentState()
    AndroidFragment<QuestionnaireFragment>(
        fragmentState = fragmentState,
        modifier = modifier
            .fillMaxSize()
            .testIdentifier(QuestionnaireComposableTestIdentifiers.ROOT),
        arguments = arguments,
    ) { fragment ->
        fragment.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY
        ) { _, _ ->
            val response = fragment.getQuestionnaireResponse()
            onResult(QuestionnaireResult.Completed(response))
        }
        fragment.setFragmentResultListener(
            QuestionnaireFragment.CANCEL_REQUEST_KEY
        ) { _, _ ->
            when (cancelBehavior) {
                is CancelBehavior.Disabled -> {
                    println("Unexpected call to cancel request with cancelBehavior set to Disabled.")
                }
                is CancelBehavior.ShouldConfirmCancel -> {
                    showsCancelAlert.value = true
                }
                is CancelBehavior.Cancel -> {
                    onResult(QuestionnaireResult.Cancelled)
                }
            }
        }
    }
}

enum class QuestionnaireComposableTestIdentifiers {
    ROOT,
}

@Composable
private fun QuestionnaireCancelAlert(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(text = "Cancel")
        },
        text = {
            Text(text = "Do you really want to cancel?")
        },
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(StringResource("OK").text())
            }
        }
    )
}
