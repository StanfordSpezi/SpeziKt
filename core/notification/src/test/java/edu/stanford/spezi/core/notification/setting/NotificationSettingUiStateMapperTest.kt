package edu.stanford.spezi.core.notification.setting

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotificationSettingUiStateMapperTest {

    private val notificationSettingUiStateMapper: NotificationSettingUiStateMapper =
        NotificationSettingUiStateMapper()

    @Test
    fun `mapSwitchChanged with appointment switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesAppointmentReminders = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.APPOINTMENT,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesAppointmentReminders).isTrue()
    }

    @Test
    fun `mapSwitchChanged with medication switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesMedicationUpdates = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.MEDICATION,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesMedicationUpdates).isTrue()
    }

    @Test
    fun `mapSwitchChanged with questionnaire switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesQuestionnaireReminders = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.QUESTIONNAIRE,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesQuestionnaireReminders).isTrue()
    }

    @Test
    fun `mapSwitchChanged with recommendation switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesRecommendationUpdates = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.RECOMMENDATION,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesRecommendationUpdates).isTrue()
    }

    @Test
    fun `mapSwitchChanged with vitals switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesVitalsReminders = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.VITALS,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesVitalsReminders).isTrue()
    }

    @Test
    fun `mapSwitchChanged with weight switch returns updated state`() {
        // Given
        val notificationSettings =
            NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
                NotificationSettings().copy(
                    receivesWeightAlerts = false
                )
            )

        // When
        val action = NotificationSettingViewModel.Action.SwitchChanged(
            switchType = NotificationSettingViewModel.SwitchType.WEIGHT,
            isChecked = true
        )
        val result = notificationSettingUiStateMapper.mapSwitchChanged(action, notificationSettings)

        // Then
        assertThat(result).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val successState = result as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(successState.notificationSettings.receivesWeightAlerts).isTrue()
    }
}
