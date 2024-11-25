package edu.stanford.bdh.engagehf.questionnaire.spezi

sealed interface CancelBehavior {
    data object Disabled : CancelBehavior
    data object ShouldConfirmCancel : CancelBehavior
    data object Cancel : CancelBehavior
}
