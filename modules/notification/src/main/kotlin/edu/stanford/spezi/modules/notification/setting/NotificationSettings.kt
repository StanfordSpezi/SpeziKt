package edu.stanford.spezi.modules.notification.setting

internal enum class NotificationType(
    val key: String,
    val section: Section,
) {
    APPOINTMENT_REMINDERS(key = "receivesAppointmentReminders", section = Section.REMINDERS),
    MEDICATION_UPDATES(key = "receivesMedicationUpdates", section = Section.UPDATES),
    QUESTIONNAIRE_REMINDERS(key = "receivesQuestionnaireReminders", section = Section.REMINDERS),
    RECOMMENDATION_UPDATES(key = "receivesRecommendationUpdates", section = Section.UPDATES),
    VITALS_REMINDERS(key = "receivesVitalsReminders", section = Section.REMINDERS),
    WEIGHT_ALERTS(key = "receivesWeightAlerts", section = Section.TRENDS),
    ;

    enum class Section {
        REMINDERS, UPDATES, TRENDS
    }
}

internal data class NotificationSettings(
    private val settings: Map<NotificationType, Boolean>,
) : Map<NotificationType, Boolean> by settings {

    override fun get(key: NotificationType): Boolean {
        return settings[key] ?: false
    }

    fun update(type: NotificationType, value: Boolean): NotificationSettings {
        val newSettings = settings.toMutableMap()
        newSettings[type] = value
        return NotificationSettings(settings = newSettings.toMap())
    }
}
