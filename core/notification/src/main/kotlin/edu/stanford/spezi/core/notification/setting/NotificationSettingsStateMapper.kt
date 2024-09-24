package edu.stanford.spezi.core.notification.setting

import javax.inject.Inject

/**
 * Maps [NotificationSettingViewModel.Action] to [NotificationSettingViewModel.UiState].
 */
internal class NotificationSettingsStateMapper @Inject constructor() {
    fun mapSwitchChanged(
        action: NotificationSettingViewModel.Action.SwitchChanged,
        currentSettings: NotificationSettings,
    ): NotificationSettings {
        return currentSettings.update(action.notificationType, action.isChecked)
    }
}
