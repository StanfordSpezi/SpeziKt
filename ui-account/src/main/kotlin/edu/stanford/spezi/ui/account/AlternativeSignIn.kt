package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying alternative sign in options.
 *
 * @param divider The [LabeledHorizontalDivider] to display.
 * @param buttons The list of [AsyncTextButton]s to display.
 */
data class AlternativeSignIn(
    val divider: LabeledHorizontalDivider,
    val buttons: List<AsyncTextButton>,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.medium),
        ) {
            divider.Content(modifier = Modifier.fillMaxWidth())

            buttons.forEach {
                it.Content(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val content = AlternativeSignIn(
        divider = LabeledHorizontalDivider(
            label = StringResource("or"),
        ),
        buttons = listOf(
            AsyncTextButton(
                title = StringResource("Sign in with Google"),
                icon = ImageResource(R.drawable.ic_google),
                action = {}
            ),
            AsyncTextButton(
                title = StringResource("Sign in with Stanford"),
                icon = ImageResource(Icons.Default.School),
                action = {}
            )
        )
    )
    SpeziTheme {
        content.Content()
    }
}
