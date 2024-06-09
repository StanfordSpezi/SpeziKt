package edu.stanford.spezi.app.account

import edu.stanford.spezi.core.navigation.ActionProvider
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import javax.inject.Inject

class AccountRegisterOnboardingActionProvider @Inject internal constructor(
    private val navigator: Navigator,
) : ActionProvider {

    override fun provideContinueButtonAction(): () -> Unit {
        return {
            navigator.navigateTo(OnboardingNavigationEvent.ConsentScreen)
        }
    }
}
