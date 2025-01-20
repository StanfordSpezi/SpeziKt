package edu.stanford.bdh.heartbeat.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.utils.extensions.testIdentifier

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
                    Text(
                        modifier = Modifier.testIdentifier(TestIdentifier.TEXT),
                        text = "Hello HeartBeat App",
                        style = TextStyles.headlineLarge,
                        color = Colors.primary
                    )
                }
            }
        }
    }

    enum class TestIdentifier {
        ROOT,
        TEXT,
    }
}
