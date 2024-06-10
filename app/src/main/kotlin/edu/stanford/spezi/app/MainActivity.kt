package edu.stanford.spezi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.app.bluetooth.screen.BluetoothScreen
import edu.stanford.spezi.app.navigation.AppNavigationEvent
import edu.stanford.spezi.app.navigation.RegisterParams
import edu.stanford.spezi.app.navigation.Routes
import edu.stanford.spezi.app.navigation.serializableType
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.login.LoginScreen
import edu.stanford.spezi.module.account.register.RegisterScreen
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.consent.ConsentScreen
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreen
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreen
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    @Dispatching.Main
    lateinit var mainDispatcher: CoroutineDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            SpeziTheme {
                Navigator(navHostController = navHostController)
                AppContent(navHostController = navHostController)
            }
        }
    }

    @Composable
    private fun AppContent(navHostController: NavHostController) {
        NavHost(
            navController = navHostController,
            startDestination = Routes.OnboardingScreen,
        ) {
            registerAppGraph()
        }
    }

    private fun NavGraphBuilder.registerAppGraph() {
        composable<Routes.RegisterScreen>(
            typeMap = mapOf(
                typeOf<RegisterParams>() to serializableType<RegisterParams>()
            )
        ) {
            val args = it.toRoute<Routes.RegisterScreen>()
            RegisterScreen(
                args.registerParams.isGoogleSignUp,
                args.registerParams.email,
                args.registerParams.password
            )
        }

        composable<Routes.LoginScreen> {
            val args = it.toRoute<Routes.LoginScreen>()
            LoginScreen(isAlreadyRegistered = args.isAlreadyRegistered)
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

    @Composable
    private fun Navigator(navHostController: NavHostController) {
        LaunchedEffect(key1 = Unit) {
            launch(mainDispatcher) {
                viewModel.getNavigationEvents().collect { event ->
                    when (event) {
                        is AccountNavigationEvent.RegisterScreen -> navHostController.navigate(
                            Routes.RegisterScreen(
                                registerParams = RegisterParams(
                                    isGoogleSignUp = event.isGoogleSignUp,
                                    email = event.email,
                                    password = event.password
                                ),
                            )
                        )

                        is AccountNavigationEvent.LoginScreen -> navHostController.navigate(
                            Routes.LoginScreen(
                                isAlreadyRegistered = event.isAlreadyRegistered
                            )
                        )

                        is AppNavigationEvent.BluetoothScreen -> navHostController.navigate(Routes.BluetoothScreen)
                        is OnboardingNavigationEvent.InvitationCodeScreen -> navHostController.navigate(
                            Routes.InvitationCodeScreen
                        )

                        is OnboardingNavigationEvent.OnboardingScreen -> navHostController.navigate(Routes.OnboardingScreen)
                        is OnboardingNavigationEvent.SequentialOnboardingScreen -> navHostController.navigate(
                            Routes.SequentialOnboardingScreen
                        )

                        is OnboardingNavigationEvent.ConsentScreen -> navHostController.navigate(Routes.ConsentScreen)
                    }
                }
            }
        }
    }
}
