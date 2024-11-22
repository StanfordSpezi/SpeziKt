package edu.stanford.spezi.core.design.views.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.views.views.views.text.MarkdownBytes
import edu.stanford.spezi.core.design.views.views.views.text.MarkdownString
import kotlinx.coroutines.delay
import java.nio.charset.StandardCharsets
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MarkdownTestComposable() {
    Column {
        MarkdownBytes(
            bytes = {
                delay(500.milliseconds)
                "This is a markdown **example** taking half a second to load."
                    .toByteArray(StandardCharsets.UTF_8)
            }
        )

        MarkdownString("This is a markdown **example**.")
    }
}
