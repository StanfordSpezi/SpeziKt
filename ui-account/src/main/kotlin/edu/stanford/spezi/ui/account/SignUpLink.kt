package edu.stanford.spezi.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying a sign up link.
 *
 * @param infoText The text to display before the link.
 * @param signUpText The text to display for the link.
 * @param onClick The action to perform when the link is clicked.
 */
data class SignUpLink(
    val infoText: StringResource,
    val signUpText: StringResource,
    val onClick: () -> Unit,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.small),
        ) {
            Text(text = infoText.text())
            Text(
                modifier = Modifier.clickable(onClick = onClick),
                text = signUpText.text(),
                color = Colors.primary,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        SignUpLink(
            infoText = StringResource("Don't have an account yet?"),
            signUpText = StringResource("Sign up"),
            onClick = {}
        ).Content()
    }
}
