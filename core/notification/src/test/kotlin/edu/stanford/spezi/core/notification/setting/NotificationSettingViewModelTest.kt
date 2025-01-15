package edu.stanford.spezi.core.notification.setting

import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.notification.NotificationPermissions
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.utils.MessageNotifier
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NotificationSettingViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repository: NotificationSettingsRepository = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)
    private val uiStateMapper: NotificationSettingsStateMapper = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val notificationPermissions: NotificationPermissions = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private val viewModel: NotificationSettingViewModel by lazy {
        NotificationSettingViewModel(
            repository = repository,
            navigator = navigator,
            notificationSettingsMapper = uiStateMapper,
            messageNotifier = messageNotifier,
            notificationPermissions = notificationPermissions,
            context = context,
        )
    }

    @Test
    fun `it should have correct initial state`() = runTest {
        // Given
        val initialState = NotificationSettingViewModel.UiState.Loading

        // When
        val uiState = viewModel.uiState.value

        // Then
        assertThat(uiState).isEqualTo(initialState)
    }

    @Test
    fun `it should have missing permissions state if required permissions are not empty`() = runTest {
        // Given
        val permissions = setOf("permission1", "permission2")
        every { notificationPermissions.getRequiredPermissions() } returns permissions
        val expectedState = NotificationSettingViewModel.UiState.MissingPermissions(permissions)

        // When
        val uiState = viewModel.uiState.value

        // Then
        assertThat(uiState).isEqualTo(expectedState)
    }

    @Test
    fun `it should clear granted permission`() = runTest {
        // Given
        val granted = "to-be-granted-permission"
        val nonGranted = "non-granted-permission"
        val permissions = setOf(granted, nonGranted)
        every { notificationPermissions.getRequiredPermissions() } returns permissions
        val initialState = viewModel.uiState.value

        // When
        viewModel.onAction(NotificationSettingViewModel.Action.PermissionResult(permission = granted, granted = true))
        viewModel.onAction(NotificationSettingViewModel.Action.PermissionResult(permission = nonGranted, granted = false))
        val newState = viewModel.uiState.value

        // Then
        assertThat(initialState).isEqualTo(NotificationSettingViewModel.UiState.MissingPermissions(permissions))
        assertThat(newState).isEqualTo(
            NotificationSettingViewModel.UiState.MissingPermissions(setOf(nonGranted))
        )
    }

    @Test
    fun `loadNotificationSettings should update state on success`() = runTest {
        // Given
        val notificationSettings = NotificationSettings(
            mapOf(NotificationType.APPOINTMENT_REMINDERS to true),
        )
        coEvery { repository.observeNotificationSettings() } returns flowOf(
            Result.success(
                notificationSettings
            )
        )

        // When
        val uiState = viewModel.uiState.value

        // Then
        assertThat(uiState).isInstanceOf(NotificationSettingViewModel.UiState.NotificationSettingsLoaded::class.java)
        val loadedState = uiState as NotificationSettingViewModel.UiState.NotificationSettingsLoaded
        assertThat(loadedState.notificationSettings).isEqualTo(notificationSettings)
    }

    @Test
    fun `loadNotificationSettings should update state on failure`() = runTest {
        // Given
        coEvery { repository.observeNotificationSettings() } returns flowOf(
            Result.failure(
                Exception(
                    "Error"
                )
            )
        )

        // When
        val uiState = viewModel.uiState.value

        // Then
        assertThat(uiState).isInstanceOf(NotificationSettingViewModel.UiState.Error::class.java)
        val errorState = uiState as NotificationSettingViewModel.UiState.Error
        assertThat(errorState.message).isEqualTo("Failed to observe notification settings")
    }

    @Test
    fun `onAction Back should navigate back`() {
        // When
        viewModel.onAction(NotificationSettingViewModel.Action.Back)

        // Then
        verify { navigator.navigateTo(NavigationEvent.PopBackStack) }
    }
}