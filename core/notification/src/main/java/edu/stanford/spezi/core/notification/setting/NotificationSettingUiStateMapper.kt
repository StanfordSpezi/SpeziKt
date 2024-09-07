package edu.stanford.spezi.core.notification.setting

import javax.inject.Inject

/**
 * Maps [NotificationSettingViewModel.Action] to [NotificationSettingViewModel.UiState].
 */
class NotificationSettingUiStateMapper @Inject constructor() {
    fun mapSwitchChanged(
        action: NotificationSettingViewModel.Action.SwitchChanged,
        it: NotificationSettingViewModel.UiState.NotificationSettingsLoaded,
    ): NotificationSettingViewModel.UiState {
        return when (action.switchType) {
            NotificationSettingViewModel.SwitchType.APPOINTMENT -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesAppointmentReminders = action.isChecked)
            )

            NotificationSettingViewModel.SwitchType.MEDICATION -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesMedicationUpdates = action.isChecked)
            )

            NotificationSettingViewModel.SwitchType.QUESTIONNAIRE -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesQuestionnaireReminders = action.isChecked)
            )

            NotificationSettingViewModel.SwitchType.RECOMMENDATION -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesRecommendationUpdates = action.isChecked)
            )

            NotificationSettingViewModel.SwitchType.VITALS -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesVitalsReminders = action.isChecked)
            )

            NotificationSettingViewModel.SwitchType.WEIGHT -> it.copy(
                notificationSettings = it.notificationSettings.copy(receivesWeightAlerts = action.isChecked)
            )
        }
    }
}
