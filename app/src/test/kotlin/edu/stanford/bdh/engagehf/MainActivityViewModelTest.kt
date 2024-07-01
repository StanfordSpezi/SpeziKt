package edu.stanford.bdh.engagehf

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.account.manager.UserState
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val accountEventsFlow = MutableSharedFlow<AccountEvents.Event>()
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)
    private val userStateFlow = MutableStateFlow<UserState>(UserState.NotInitialized)
    private val userSessionManager: UserSessionManager = mockk()
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setUp() {
        every { accountEvents.events } returns accountEventsFlow
        every { userSessionManager.userState } returns userStateFlow
        viewModel = MainActivityViewModel(
            accountEvents = accountEvents,
            navigator = navigator,
            userSessionManager = userSessionManager,
        )
    }

    @Test
    fun `it should start observing on init`() {
        verify { accountEvents.events }
        verify { userSessionManager.userState }
    }

    @Test
    fun `it should navigate to bluetooth screen on SignUpSuccess event`() = runTestUnconfined {
        // given
        val event = AccountEvents.Event.SignUpSuccess

        // when
        accountEventsFlow.emit(event)

        // then
        verify { navigator.navigateTo(event = AppNavigationEvent.BluetoothScreen) }
    }

    @Test
    fun `it should navigate to bluetooth screen on SignInSuccess event`() = runTestUnconfined {
        // given
        val event = AccountEvents.Event.SignInSuccess

        // when
        accountEventsFlow.emit(event)

        // then
        verify { navigator.navigateTo(event = AppNavigationEvent.BluetoothScreen) }
    }

    @Test
    fun `it should not navigate on other account events`() = runTestUnconfined {
        // given
        val event = AccountEvents.Event.SignInFailure

        // when
        accountEventsFlow.emit(event)

        // then
        verify { navigator wasNot Called }
    }

    @Test
    fun `it should return navigation events`() {
        // given
        val events: SharedFlow<NavigationEvent> = mockk()
        every { navigator.events } returns events

        // when
        val result = viewModel.getNavigationEvents()

        // then
        assertThat(result).isEqualTo(events)
    }

    @Test
    fun `it should navigate to bluetooth screen for registered user if consented`() =
        runTestUnconfined {
            // given
            val userState = UserState.Registered(hasConsented = true)

            // when
            userStateFlow.update { userState }

            // then
            verify { navigator.navigateTo(event = AppNavigationEvent.BluetoothScreen) }
        }

    @Test
    fun `it should navigate to consent screen for registered user if not consented`() =
        runTestUnconfined {
            // given
            val userState = UserState.Registered(hasConsented = false)

            // when
            userStateFlow.update { userState }

            // then
            verify { navigator.navigateTo(event = OnboardingNavigationEvent.ConsentScreen) }
        }

    @Test
    fun `it should not navigate for not initialized users`() =
        runTestUnconfined {
            // given
            val userState = UserState.NotInitialized

            // when
            userStateFlow.update { userState }

            // then
            verify { navigator wasNot Called }
        }

    @Test
    fun `it should not navigate for anonymous users`() =
        runTestUnconfined {
            // given
            val userState = UserState.Anonymous

            // when
            userStateFlow.update { userState }

            // then
            verify { navigator wasNot Called }
        }
}
