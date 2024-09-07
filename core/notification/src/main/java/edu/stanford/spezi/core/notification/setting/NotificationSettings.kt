package edu.stanford.spezi.core.notification.setting

/**
 * Data class for storing user's notification preferences.
 * users/uid/
 */
data class NotificationSettings(
    val receivesAppointmentReminders: Boolean = false,
    val receivesMedicationUpdates: Boolean = false,
    val receivesQuestionnaireReminders: Boolean = false,
    val receivesRecommendationUpdates: Boolean = false,
    val receivesVitalsReminders: Boolean = false,
    val receivesWeightAlerts: Boolean = false,
)
