package edu.stanford.spezi.app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import edu.stanford.spezi.app.bluetooth.screen.BluetoothScreen
import edu.stanford.spezi.module.account.login.LoginScreen
import edu.stanford.spezi.module.account.register.RegisterScreen
import edu.stanford.spezi.module.onboarding.consent.ConsentScreen
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreen
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreen
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingViewPagerScreen


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun NavGraphBuilder.mainGraph() {
    composable<Routes.RegisterScreen> {
        RegisterScreen()
    }

    composable<Routes.LoginScreen> {
        LoginScreen()
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
        SequentialOnboardingViewPagerScreen()
    }

    composable<Routes.ConsentScreen> {
        ConsentScreen()
    }
}
