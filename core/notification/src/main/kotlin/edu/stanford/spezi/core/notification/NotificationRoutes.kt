package edu.stanford.spezi.core.notification

import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationRoutes {
    @Serializable
    data object NotificationSetting : NotificationRoutes()
}
