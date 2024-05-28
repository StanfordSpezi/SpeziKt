package edu.stanford.spezi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.app.bluetooth.screen.BluetoothScreen
import edu.stanford.spezi.core.design.theme.SpeziTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeziTheme {
                BluetoothScreen()
            }
        }
    }
}
