package edu.stanford.spezi.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying a forgot password link.
 *
 * @param text The text to display.
 * @param onClick The action to perform when the link is clicked.
 */
data class ForgotPasswordLink(
    val text: StringResource,
    val onClick: () -> Unit,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            modifier = modifier.clickable(onClick = onClick),
            text = text.text(),
            color = Colors.primary,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        ForgotPasswordLink(
            text = StringResource("Forgot password?"),
            onClick = {}
        ).Content()
    }
}
