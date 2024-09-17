package edu.stanford.spezi.core.notification.setting

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotificationSettingsStateMapperTest {

    private val notificationSettingsStateMapper: NotificationSettingsStateMapper =
        NotificationSettingsStateMapper()

    @Test
    fun `mapSwitchChanged should update notification settings`() {
        val currentSettings = NotificationSettings(
            mapOf(NotificationType.APPOINTMENT_REMINDERS to true),
        )
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            NotificationType.APPOINTMENT_REMINDERS,
            false
        )
        val updatedSettings =
            notificationSettingsStateMapper.mapSwitchChanged(action, currentSettings)
        assertThat(updatedSettings).isEqualTo(
            NotificationSettings(
                mapOf(NotificationType.APPOINTMENT_REMINDERS to false),
            )
        )
    }
}
