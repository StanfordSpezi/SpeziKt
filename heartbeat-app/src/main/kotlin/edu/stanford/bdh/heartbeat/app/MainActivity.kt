package edu.stanford.bdh.heartbeat.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.bdh.heartbeat.app.main.MainPage
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.testing.testIdentifier

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeziTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testIdentifier(TestIdentifier.ROOT),
                    contentAlignment = Alignment.Center
                ) {
                    MainPage()
                }
            }
        }
    }

    enum class TestIdentifier {
        ROOT,
        TEXT,
    }
}
