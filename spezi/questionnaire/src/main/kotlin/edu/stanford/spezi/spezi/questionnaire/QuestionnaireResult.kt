package edu.stanford.spezi.spezi.questionnaire

import org.hl7.fhir.r4.model.QuestionnaireResponse

sealed interface QuestionnaireResult {
    data object Cancelled : QuestionnaireResult
    data object Failed : QuestionnaireResult
    data class Completed(val response: QuestionnaireResponse) : QuestionnaireResult
}
