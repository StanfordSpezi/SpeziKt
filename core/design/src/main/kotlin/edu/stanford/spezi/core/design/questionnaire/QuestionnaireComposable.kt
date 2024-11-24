package edu.stanford.spezi.core.design.questionnaire

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.component.StringResource.Companion.invoke
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire

@Composable
fun QuestionnaireComposable(
    questionnaire: Questionnaire,
    modifier: Modifier = Modifier,
    completionStepMessage: String? = null, // Is there a simple way to add a completion message?
    cancelBehavior: CancelBehavior = CancelBehavior.ShouldConfirmCancel,
    onResult: suspend (QuestionnaireResult) -> Unit,
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
        FhirContext.forR4()
            .newJsonParser()
            .encodeResourceToString(fullQuestionnaire)
    }

    QuestionnaireComposable(
        questionnaireJson = questionnaireJson,
        modifier = modifier,
        cancelBehavior = cancelBehavior,
        onResult = onResult
    )
}

@Composable
fun QuestionnaireComposable(
    questionnaireJson: String,
    modifier: Modifier = Modifier,
    cancelBehavior: CancelBehavior = CancelBehavior.ShouldConfirmCancel,
    onResult: suspend (QuestionnaireResult) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val showsCancelAlert = remember { mutableStateOf(false) }
    if (showsCancelAlert.value) {
        QuestionnaireCancelAlert {
            coroutineScope.launch {
                onResult(QuestionnaireResult.Cancelled)
            }
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
        modifier = modifier.fillMaxSize(),
        arguments = arguments,
    ) { fragment ->
        fragment.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY
        ) { _, _ ->
            coroutineScope.launch {
                val response = fragment.getQuestionnaireResponse()
                onResult(QuestionnaireResult.Completed(response))
            }
        }
        fragment.setFragmentResultListener(
            QuestionnaireFragment.CANCEL_REQUEST_KEY
        ) { _, _ ->
            coroutineScope.launch {
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

@ThemePreviews
@Composable
private fun QuestionnaireComposablePreview() {
    SpeziTheme(isPreview = true) {
        // TODO: Preview is unfortunately not working due to missing "FragmentActivity"
        //  - is there a fix for that or simply no way to preview this?
        QuestionnaireComposable(
            remember { Questionnaire() }
        ) {
            println("Received result: $it")
        }
    }
}
