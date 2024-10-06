package edu.stanford.bdh.engagehf

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.Routes
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.account.manager.UserState
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val accountEventsFlow = MutableSharedFlow<AccountEvents.Event>()
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)
    private val userSessionManager: UserSessionManager = mockk()
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setUp() {
        every { accountEvents.events } returns accountEventsFlow
        coEvery { userSessionManager.getUserState() } returns UserState.NotInitialized
    }

    @Test
    fun `it should start observing on init`() {
        // when
        createViewModel()

        // then
        verify { accountEvents.events }
        coVerify { userSessionManager.getUserState() }
    }

    @Test
    fun `it should navigate to InvitationCodeScreen on SignUpSuccess event with hasInvitationCodeConfirmed false`() =
        runTestUnconfined {
            // given
            createViewModel()
            val event = AccountEvents.Event.SignUpSuccess
            coEvery { userSessionManager.getUserState() } returns UserState.Registered(
                hasInvitationCodeConfirmed = false
            )

            // when
            accountEventsFlow.emit(event)

            // then
            verify { navigator.navigateTo(event = OnboardingNavigationEvent.InvitationCodeScreen) }
        }

    @Test
    fun `it should navigate to app screen on SignUpSuccess event with hasInvitationCodeConfirmed true`() =
        runTestUnconfined {
            // given
            createViewModel()
            val event = AccountEvents.Event.SignUpSuccess
            coEvery { userSessionManager.getUserState() } returns UserState.Registered(
                hasInvitationCodeConfirmed = true
            )

            // when
            accountEventsFlow.emit(event)

            // then
            verify { navigator.navigateTo(event = AppNavigationEvent.AppScreen(clearStackTrace = true)) }
        }

    @Test
    fun `it should navigate to OnboardingScreen when user state is NotInitialized`() =
        runTestUnconfined {
            // given
            coEvery { userSessionManager.getUserState() } returns UserState.NotInitialized
            createViewModel()

            // when
            accountEventsFlow.emit(AccountEvents.Event.SignInSuccess)

            // then
            verify {
                navigator.navigateTo(
                    event = OnboardingNavigationEvent.OnboardingScreen(
                        clearBackStack = true
                    )
                )
            }
        }

    @Test
    fun `it should navigate to ClearBackStackOnboarding when user account event is SignOutSuccess`() =
        runTestUnconfined {
            // given
            coEvery { userSessionManager.getUserState() } returns UserState.NotInitialized
            createViewModel()

            // when
            accountEventsFlow.emit(AccountEvents.Event.SignOutSuccess)

            // then
            verify {
                navigator.navigateTo(
                    event = OnboardingNavigationEvent.OnboardingScreen(
                        clearBackStack = true
                    )
                )
            }
        }

    @Test
    fun `it should notify message when user account event is SignOutFailure`() = runTestUnconfined {
        // given
        coEvery { userSessionManager.getUserState() } returns UserState.NotInitialized
        createViewModel()

        // when
        accountEventsFlow.emit(AccountEvents.Event.SignOutFailure)

        // then
        verify { messageNotifier.notify(R.string.sign_out_failed) }
    }

    @Test
    fun `it should navigate to InvitationCodeScreen on SignInSuccess event with hasInvitationCodeConfirmed false`() =
        runTestUnconfined {
            // given
            createViewModel()
            val event = AccountEvents.Event.SignInSuccess
            coEvery { userSessionManager.getUserState() } returns UserState.Registered(
                hasInvitationCodeConfirmed = false
            )

            // when
            accountEventsFlow.emit(event)

            // then
            verify { navigator.navigateTo(event = OnboardingNavigationEvent.InvitationCodeScreen) }
        }

    @Test
    fun `it should navigate to app screen on SignInSuccess event with hasInvitationCodeConfirmed true`() =
        runTestUnconfined {
            // given
            createViewModel()
            val event = AccountEvents.Event.SignInSuccess
            coEvery { userSessionManager.getUserState() } returns UserState.Registered(
                hasInvitationCodeConfirmed = true
            )

            // when
            accountEventsFlow.emit(event)

            // then
            verify { navigator.navigateTo(event = AppNavigationEvent.AppScreen(clearStackTrace = true)) }
        }

    @Test
    fun `it should not navigate on other account events`() = runTestUnconfined {
        // given
        createViewModel()
        val event = AccountEvents.Event.SignInFailure

        // when
        accountEventsFlow.emit(event)

        // then
        verify { navigator wasNot Called }
    }

    @Test
    fun `it should return navigation events`() {
        // given
        createViewModel()
        val events: SharedFlow<NavigationEvent> = mockk()
        every { navigator.events } returns events

        // when
        val result = viewModel.getNavigationEvents()

        // then
        assertThat(result).isEqualTo(events)
    }

    @Test
    fun `it should have app screen start destination for registered user if has Invitation Code Confirmed`() =
        runTestUnconfined {
            // given
            val userState = UserState.Registered(hasInvitationCodeConfirmed = true)
            coEvery { userSessionManager.getUserState() } returns userState

            // when
            createViewModel()

            // then
            assertStartDestination(startDestination = Routes.AppScreen)
        }

    @Test
    fun `it should have consent screen start destination for registered user if has not Invitation Code Confirmed`() =
        runTestUnconfined {
            // given
            val userState = UserState.Registered(hasInvitationCodeConfirmed = false)
            coEvery { userSessionManager.getUserState() } returns userState

            // when
            createViewModel()

            // then
            assertStartDestination(startDestination = Routes.InvitationCodeScreen)
        }

    @Test
    fun `it should have OnboardingScreen start destination for not initialized users`() =
        runTestUnconfined {
            // given
            val userState = UserState.NotInitialized
            coEvery { userSessionManager.getUserState() } returns userState

            // when
            createViewModel()

            // then
            assertStartDestination(startDestination = Routes.OnboardingScreen)
        }

    private fun assertStartDestination(startDestination: Routes) {
        val content = MainUiState.Content(startDestination = startDestination)
        assertThat(viewModel.uiState.value).isEqualTo(content)
    }

    private fun createViewModel() {
        viewModel = MainActivityViewModel(
            accountEvents = accountEvents,
            navigator = navigator,
            userSessionManager = userSessionManager,
            messageNotifier = messageNotifier,
        )
    }
}
