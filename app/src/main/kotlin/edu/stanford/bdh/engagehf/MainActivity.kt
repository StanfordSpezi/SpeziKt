package edu.stanford.bdh.engagehf

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
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.RegisterParams
import edu.stanford.bdh.engagehf.navigation.Routes
import edu.stanford.bdh.engagehf.navigation.screens.AppScreen
import edu.stanford.bdh.engagehf.navigation.serializableType
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.login.LoginScreen
import edu.stanford.spezi.module.account.register.RegisterScreen
import edu.stanford.spezi.module.onboarding.OnboardingNavigationEvent
import edu.stanford.spezi.module.onboarding.consent.ConsentScreen
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

    @Inject
    @Dispatching.Main
    lateinit var mainDispatcher: CoroutineDispatcher

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
                args.registerParams.isGoogleSignUp,
                args.registerParams.email,
                args.registerParams.password
            )
        }

        composable<Routes.LoginScreen> {
            val args = it.toRoute<Routes.LoginScreen>()
            LoginScreen(isAlreadyRegistered = args.isAlreadyRegistered)
        }

        composable<EducationRoutes.VideoDetail>(
            typeMap = mapOf(
                typeOf<Video>() to serializableType<Video>()
            )
        ) {
            VideoScreen()
        }

        composable<Routes.AppScreen> {
            AppScreen()
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

                        is OnboardingNavigationEvent.InvitationCodeScreen -> navHostController.navigate(
                            Routes.InvitationCodeScreen
                        )

                        is OnboardingNavigationEvent.OnboardingScreen -> navHostController.navigate(
                            Routes.OnboardingScreen
                        )

                        is OnboardingNavigationEvent.SequentialOnboardingScreen -> navHostController.navigate(
                            Routes.SequentialOnboardingScreen
                        )

                        is OnboardingNavigationEvent.ConsentScreen -> navHostController.navigate(
                            Routes.ConsentScreen
                        )

                        is AppNavigationEvent.AppScreen -> navHostController.navigate(Routes.AppScreen)
                        is NavigationEvent.PopBackStack -> navHostController.popBackStack()
                        is NavigationEvent.NavigateUp -> navHostController.navigateUp()

                        is EducationNavigationEvent.VideoSectionClicked -> navHostController.navigate(
                            EducationRoutes.VideoDetail(
                                video = event.video
                            )
                        )
                    }
                }
            }
        }
    }
}
