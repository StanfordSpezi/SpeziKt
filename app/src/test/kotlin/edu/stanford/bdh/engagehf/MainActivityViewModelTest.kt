package edu.stanford.bdh.engagehf

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.consent.PdfService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val pdfService: PdfService = mockk(relaxed = true)
    private val firebaseUser: FirebaseUser = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { accountEvents.events } returns MutableSharedFlow()
        viewModel = MainActivityViewModel(accountEvents, navigator, firebaseAuth, pdfService)
    }

    @Test
    fun `when user is logged in and pdf is uploaded, should navigate to BluetoothScreen`() =
        runTestUnconfined {
            // given
            coEvery { firebaseAuth.currentUser } returns firebaseUser
            coEvery { firebaseUser.isAnonymous } returns false
            coEvery { pdfService.isPdfUploaded() } returns Result.success(true)

            // when
            getAuthStateListener().onAuthStateChanged(firebaseAuth)

            // then
            coVerify { navigator.navigateTo(AppNavigationEvent.BluetoothScreen) }
        }

    @Test
    fun `when user is logged in and pdf is not uploaded, should navigate to ConsentScreen`() =
        runTestUnconfined {
            // given
            coEvery { firebaseAuth.currentUser } returns firebaseUser
            coEvery { firebaseUser.isAnonymous } returns false
            coEvery { pdfService.isPdfUploaded() } returns Result.success(false)

            // when
            getAuthStateListener().onAuthStateChanged(firebaseAuth)

            // then
            coVerify { navigator.navigateTo(OnboardingNavigationEvent.ConsentScreen) }
        }

    @Test
    fun `when user is logged in and pdf is uploaded returns failure, should navigate to ConsentScreen`() =
        runTestUnconfined {
            // given
            coEvery { firebaseAuth.currentUser } returns firebaseUser
            coEvery { firebaseUser.isAnonymous } returns false
            coEvery { pdfService.isPdfUploaded() } returns Result.failure(Exception())

            // when
            getAuthStateListener().onAuthStateChanged(firebaseAuth)

            // then
            coVerify { navigator.navigateTo(OnboardingNavigationEvent.ConsentScreen) }
        }

    @Test
    fun `when no user is logged in, should not navigate`() = runTestUnconfined {
        // given
        coEvery { firebaseAuth.currentUser } returns null

        // when
        getAuthStateListener().onAuthStateChanged(firebaseAuth)

        // then
        coVerify(exactly = 0) { navigator.navigateTo(any()) }
    }

    private fun getAuthStateListener(): FirebaseAuth.AuthStateListener {
        val slot = slot<FirebaseAuth.AuthStateListener>()
        every { firebaseAuth.addAuthStateListener(capture(slot)) } answers { }
        viewModel = MainActivityViewModel(accountEvents, navigator, firebaseAuth, pdfService)
        return slot.captured
    }
}
