package edu.stanford.spezi.core.notification.notifier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMessage(
    val title: String,
    val message: String,
    val action: String?,
    val messageId: String?,
    val isDismissible: Boolean?,
) : Parcelable {
    companion object {
        const val FIREBASE_MESSAGE_KEY = "FIREBASE_MESSAGE"
    }
}
