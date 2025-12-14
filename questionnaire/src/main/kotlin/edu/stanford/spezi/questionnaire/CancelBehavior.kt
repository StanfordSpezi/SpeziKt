package edu.stanford.spezi.questionnaire

sealed interface CancelBehavior {
    data object Disabled : CancelBehavior
    data object ShouldConfirmCancel : CancelBehavior
    data object Cancel : CancelBehavior
}
