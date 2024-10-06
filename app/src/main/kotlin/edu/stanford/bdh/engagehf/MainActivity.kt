package edu.stanford.bdh.engagehf

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.bdh.engagehf.bluetooth.BluetoothViewModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.RegisterParams
import edu.stanford.bdh.engagehf.navigation.Routes
import edu.stanford.bdh.engagehf.navigation.screens.AppScreen
import edu.stanford.bdh.engagehf.navigation.serializableType
import edu.stanford.bdh.engagehf.questionnaire.QuestionnaireScreen
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.notification.NotificationNavigationEvent
import edu.stanford.spezi.core.notification.NotificationRoutes
import edu.stanford.spezi.core.notification.setting.NotificationSettingScreen
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.login.LoginScreen
import edu.stanford.spezi.module.account.register.RegisterScreen
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreen
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingScreen
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreen
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.EducationRoutes
import edu.stanford.spezi.modules.education.video.VideoScreen
import edu.stanford.spezi.modules.education.videos.Video
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    private val bluetoothViewModel by viewModels<BluetoothViewModel>()

    private val logger by speziLogger()

    @Inject
    @Dispatching.Main
    lateinit var mainDispatcher: CoroutineDispatcher

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        bluetoothViewModel.onAction(Action.NewIntent(intent))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.uiState.value is MainUiState.SplashScreen
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            SpeziTheme {
                when (val uiState = viewModel.uiState.collectAsState().value) {
                    is MainUiState.SplashScreen -> Loading()
                    is MainUiState.Content -> {
                        val navHostController = rememberNavController()
                        Navigator(navHostController = navHostController)
                        AppContent(
                            navHostController = navHostController,
                            startDestination = uiState.startDestination
                        )
                        setTheme(R.style.Theme_Spezi_Content)
                    }
                }
            }
        }
    }

    @Composable
    private fun Loading() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(Sizes.Content.large))
        }
    }

    @Composable
    private fun AppContent(
        navHostController: NavHostController,
        startDestination: Routes,
    ) {
        NavHost(
            navController = navHostController,
            startDestination = startDestination,
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
                args.registerParams.email,
                args.registerParams.password
            )
        }

        composable<Routes.LoginScreen> {
            LoginScreen()
        }

        composable<EducationRoutes.VideoDetail>(
            typeMap = mapOf(
                typeOf<Video>() to serializableType<Video>()
            )
        ) {
            VideoScreen()
        }

        composable<NotificationRoutes.NotificationSetting> {
            NotificationSettingScreen()
        }

        composable<Routes.AppScreen> {
            AppScreen()
        }

        composable<Routes.QuestionnaireScreen>(
            typeMap = mapOf(
                typeOf<String>() to serializableType<String>()
            )
        ) {
            QuestionnaireScreen()
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
                                    email = event.email,
                                    password = event.password
                                ),
                            )
                        )

                        is AppNavigationEvent.QuestionnaireScreen -> navHostController.navigate(
                            Routes.QuestionnaireScreen(event.questionnaireId)
                        )

                        is AccountNavigationEvent.LoginScreen -> navHostController.navigate(
                            Routes.LoginScreen()
                        )

                        is OnboardingNavigationEvent.InvitationCodeScreen -> navHostController.navigate(
                            Routes.InvitationCodeScreen
                        )

                        is OnboardingNavigationEvent.OnboardingScreen -> navHostController.navigateTo(
                            Routes.OnboardingScreen, event.clearBackStack
                        )

                        is OnboardingNavigationEvent.SequentialOnboardingScreen -> navHostController.navigate(
                            Routes.SequentialOnboardingScreen
                        )

                        is OnboardingNavigationEvent.ConsentScreen -> navHostController.navigate(
                            Routes.ConsentScreen
                        )

                        is AppNavigationEvent.AppScreen -> navHostController.navigateTo(
                            route = Routes.AppScreen,
                            clearBackStack = event.clearStackTrace
                        )

                        is NavigationEvent.PopBackStack -> navHostController.popBackStack()
                        is NavigationEvent.NavigateUp -> navHostController.navigateUp()

                        is EducationNavigationEvent.VideoSectionClicked -> navHostController.navigate(
                            EducationRoutes.VideoDetail(
                                video = event.video
                            )
                        )

                        is NotificationNavigationEvent.NotificationSettings -> navHostController.navigate(
                            NotificationRoutes.NotificationSetting
                        )
                    }
                }
            }
        }
    }

    private fun NavHostController.navigateTo(route: Routes, clearBackStack: Boolean = false) {
        navigate(route) {
            if (clearBackStack) {
                popUpTo(graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
