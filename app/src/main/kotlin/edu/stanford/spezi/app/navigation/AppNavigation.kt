package edu.stanford.spezi.app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import kotlinx.coroutines.launch

/**
 * The main navigation component of the app.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(navigator: Navigator) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(navigator) {
        coroutineScope.launch {
            navigator.events.collect { event ->
                when (event) {
                    is NavigationEvent.RegisterScreen -> navController.navigate(Routes.RegisterScreen)
                    is NavigationEvent.LoginScreen -> navController.navigate(Routes.LoginScreen)
                    is NavigationEvent.BluetoothScreen -> navController.navigate(Routes.BluetoothScreen)
                    is NavigationEvent.InvitationCodeScreen -> navController.navigate(Routes.InvitationCodeScreen)
                    is NavigationEvent.OnboardingScreen -> navController.navigate(Routes.OnboardingScreen)
                    is NavigationEvent.SequentialOnboardingScreen -> navController.navigate(Routes.SequentialOnboardingScreen)
                    is NavigationEvent.ConsentScreen -> navController.navigate(Routes.ConsentScreen)
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