package edu.stanford.spezi.core.notification.notifier

data class FirebaseMessage(
    val title: String,
    val message: String,
    val action: String?,
    val messageId: String?,
    val isDismissible: Boolean?,
) {
    companion object {
        const val ACTION_KEY = "action"
        const val MESSAGE_ID_KEY = "messageId"
        const val IS_DISMISSIBLE_KEY = "isDismissible"
    }
}
