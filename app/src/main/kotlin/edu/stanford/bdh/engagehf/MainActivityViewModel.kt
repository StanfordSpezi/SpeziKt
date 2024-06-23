package edu.stanford.bdh.engagehf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.consent.PdfService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountEvents: AccountEvents,
    private val navigator: Navigator,
    private val firebaseAuth: FirebaseAuth,
    private val pdfService: PdfService,
) : ViewModel() {
    private val logger by speziLogger()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        user?.let {
            if (!it.isAnonymous) {
                // If the user is not anonymous, we can check if the PDF has been uploaded
                viewModelScope.launch {
                    if (pdfService.isPdfUploaded().isSuccess) {
                        navigator.navigateTo(AppNavigationEvent.BluetoothScreen)
                    } else {
                        // User has to consent to the study before proceeding and upload the PDF
                        navigator.navigateTo(OnboardingNavigationEvent.ConsentScreen)
                    }
                }
            }
            // If the user is anonymous, we don't need to check for the PDF and user stays on the start screen
        }
    }

    init {
        viewModelScope.launch {
            firebaseAuth.addAuthStateListener(authStateListener)
            accountEvents.events.collect { event ->
                when (event) {
                    is AccountEvents.Event.SignUpSuccess, AccountEvents.Event.SignInSuccess -> {
                        navigator.navigateTo(AppNavigationEvent.BluetoothScreen)
                    }

                    else -> {
                        logger.i { "Ignoring registration event: $event" }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    fun getNavigationEvents(): Flow<NavigationEvent> = navigator.events
}
