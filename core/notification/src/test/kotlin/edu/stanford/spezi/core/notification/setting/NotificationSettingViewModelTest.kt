package edu.stanford.spezi.core.notification.setting

import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.core.utils.PermissionChecker
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationSettingViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repository: NotificationSettingsRepository = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)
    private val uiStateMapper: NotificationSettingsStateMapper = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val permissionChecker: PermissionChecker = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private lateinit var viewModel: NotificationSettingViewModel

    @Before
    fun setup() {
        viewModel = NotificationSettingViewModel(
            repository = repository,
            navigator = navigator,
            notificationSettingsMapper = uiStateMapper,
            messageNotifier = messageNotifier,
            permissionChecker = permissionChecker,
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
        viewModel = NotificationSettingViewModel(
            repository = repository,
            navigator = navigator,
            notificationSettingsMapper = uiStateMapper,
            messageNotifier = messageNotifier,
            permissionChecker = permissionChecker,
            context = context,
        )

        // Then
        val uiState = viewModel.uiState.value
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
        viewModel = NotificationSettingViewModel(
            repository = repository,
            navigator = navigator,
            notificationSettingsMapper = uiStateMapper,
            messageNotifier = messageNotifier,
            permissionChecker = permissionChecker,
            context = context,
        )

        // Then
        val uiState = viewModel.uiState.value
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
