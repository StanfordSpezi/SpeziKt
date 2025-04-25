package edu.stanford.spezi.questionnaire

import android.content.res.Resources
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.spezi.ui.testing.testIdentifier
import org.hl7.fhir.r4.model.Questionnaire
import com.google.android.fhir.datacapture.R as DataCaptureR

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
        fragment.adjustBottomNavButtonSizesIfNeeded()
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

/**
 * On large font size and display size settings on device, the bottom nav buttons exceed the
 * available screen width and are not visible. This function adjusts the size of the buttons to
 * ensure they are visible if needed.
 */
private fun QuestionnaireFragment.adjustBottomNavButtonSizesIfNeeded() {
    val view = view ?: return
    val fontScale = Resources.getSystem().configuration.fontScale
    val displayScale = view.context.resources.displayMetrics.density
    SpeziLogger.tag(TAG).i { "System font scale: $fontScale, display scale: $displayScale" }

    if (fontScale >= MAX_FONT_SCALE && displayScale >= MAX_DISPLAY_SCALE) {
        // Iterating through the res ids of the buttons defined in questionnaire_fragment.xml of
        // fhir data capture
        listOf(
            DataCaptureR.id.cancel_questionnaire,
            DataCaptureR.id.pagination_previous_button,
            DataCaptureR.id.pagination_next_button,
            DataCaptureR.id.review_mode_button,
            DataCaptureR.id.submit_questionnaire
        ).forEach { buttonId ->
            view.findViewById<Button>(buttonId)?.let { button ->
                SpeziLogger.tag(TAG).i { "Reducing button $buttonId size" }
                button.textSize = REDUCED_BUTTON_TEXT_SIZE
                val layoutParams = button.layoutParams as? ViewGroup.MarginLayoutParams
                layoutParams?.setMargins(
                    /* left */ REDUCED_BUTTON_HORIZONTAL_MARGIN,
                    /* top */ REDUCED_BUTTON_VERTICAL_MARGIN,
                    /* right */ REDUCED_BUTTON_HORIZONTAL_MARGIN,
                    /* bottom */ REDUCED_BUTTON_VERTICAL_MARGIN
                )
                button.layoutParams = layoutParams
            }
        }
    }
}

private const val TAG = "QuestionnaireFragment"
private const val MAX_FONT_SCALE = 1.45f
private const val MAX_DISPLAY_SCALE = 3f
private const val REDUCED_BUTTON_TEXT_SIZE = 10f
private const val REDUCED_BUTTON_HORIZONTAL_MARGIN = 2
private const val REDUCED_BUTTON_VERTICAL_MARGIN = 8

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
            Text(text = stringResource(R.string.questionnaire_cancel))
        },
        text = {
            Text(text = stringResource(R.string.questionnaire_cancel_confirm_description))
        },
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(text = stringResource(R.string.questionnaire_ok))
            }
        }
    )
}
