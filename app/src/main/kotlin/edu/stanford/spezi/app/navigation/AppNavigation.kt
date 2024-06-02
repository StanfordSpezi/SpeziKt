package edu.stanford.spezi.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import kotlinx.coroutines.launch

/**
 * The main navigation component of the app.
 */
@Composable
fun AppNavigation(navigator: Navigator) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(navigator) {
        coroutineScope.launch {
            navigator.events.collect { event ->
                when (event) {
                    is AccountNavigationEvent.RegisterScreen -> navController.navigate(Routes.RegisterScreen)
                    is AccountNavigationEvent.LoginScreen -> navController.navigate(
                        Routes.LoginScreen(
                            isAlreadyRegistered = event.isAlreadyRegistered
                        )
                    )
                    is DefaultNavigationEvent.BluetoothScreen -> navController.navigate(Routes.BluetoothScreen)
                    is OnboardingNavigationEvent.InvitationCodeScreen -> navController.navigate(
                        Routes.InvitationCodeScreen
                    )

                    is OnboardingNavigationEvent.OnboardingScreen -> navController.navigate(Routes.OnboardingScreen)
                    is OnboardingNavigationEvent.SequentialOnboardingScreen -> navController.navigate(
                        Routes.SequentialOnboardingScreen
                    )

                    is OnboardingNavigationEvent.ConsentScreen -> navController.navigate(Routes.ConsentScreen)
                }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = Routes.OnboardingScreen,
    ) {
        mainGraph()
    }
}