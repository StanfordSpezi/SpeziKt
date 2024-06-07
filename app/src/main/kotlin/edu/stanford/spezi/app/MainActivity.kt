package edu.stanford.spezi.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.app.navigation.AppNavigation
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.navigation.Navigator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeziTheme {
                AppNavigation(navigator)
            }
        }
    }
}
