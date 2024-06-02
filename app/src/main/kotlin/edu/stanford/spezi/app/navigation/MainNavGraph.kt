package edu.stanford.spezi.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.stanford.spezi.app.bluetooth.screen.BluetoothScreen
import edu.stanford.spezi.module.account.login.LoginScreen
import edu.stanford.spezi.module.account.register.RegisterScreen
import edu.stanford.spezi.module.onboarding.consent.ConsentScreen
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreen
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreen
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen


fun NavGraphBuilder.mainGraph() {
    composable<Routes.RegisterScreen> {
        RegisterScreen()
    }

    composable<Routes.LoginScreen> {
        val args = it.toRoute<Routes.LoginScreen>()
        LoginScreen(args.isAlreadyRegistered)
    }

    composable<Routes.BluetoothScreen> {
        BluetoothScreen()
    }

    composable<Routes.InvitationCodeScreen> {
        InvitationCodeScreen()
    }

    composable<Routes.OnboardingScreen> {
        OnboardingScreen()
    }

    composable<Routes.SequentialOnboardingScreen> {
        SequentialOnboardingScreen()
    }

    composable<Routes.ConsentScreen> {
        ConsentScreen()
    }
}
