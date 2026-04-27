package edu.stanford.spezi.sample.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.sample.app.health.HealthScreen
import edu.stanford.spezi.sample.app.home.HomeScreen
import edu.stanford.spezi.ui.ConsumeEvents
import edu.stanford.spezi.ui.theme.SpeziTheme

class SampleActivity : AppCompatActivity() {
    private val navigator by dependency<Navigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpeziTheme {
                AppContent()
            }
        }
    }

    @Composable
    private fun AppContent() {
        val navHostController = rememberNavController()
        NavHost(
            navController = navHostController,
            startDestination = Routes.Home,
        ) {
            composable<Routes.Home> {
                HomeScreen()
            }

            composable<Routes.Health> {
                HealthScreen()
            }
        }

        ConsumeEvents(eventFlow = navigator.events) { event ->
            when (event) {
                is NavigationEvent.PopBackStack -> navHostController.popBackStack()
                is NavigationEvent.NavigateUp -> navHostController.navigateUp()
                is NavigationEvent.Health -> navHostController.navigate(Routes.Health)
            }
        }
    }
}
