package edu.stanford.spezi.testing.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import edu.stanford.spezi.testing.concurrency.MainDispatcherRule
import edu.stanford.spezi.ui.theme.SpeziTheme
import org.junit.Rule

abstract class ScreenshotTest {

    @get:Rule
    internal val paparazziRule = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "android:Theme.Material.Light.NoActionBar",
        renderingMode = SessionParams.RenderingMode.SHRINK,
        maxPercentDifference = 0.01,
    )

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    fun screenshot(content: @Composable () -> Unit) {
        paparazziRule.snapshot {
            CompositionLocalProvider(LocalInspectionMode provides true) {
                SpeziTheme {
                    content.invoke()
                }
            }
        }
    }
}
