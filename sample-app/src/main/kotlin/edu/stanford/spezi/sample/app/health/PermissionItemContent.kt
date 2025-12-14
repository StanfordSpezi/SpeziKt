package edu.stanford.spezi.sample.app.health

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class PermissionItemContent(
    val title: String,
    val status: PermissionStatus,
    val action: PermissionAction?,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        DefaultElevatedCard {
            Row(
                modifier = modifier.fillMaxWidth()
                    .padding(Spacings.small),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacings.small)) {
                    Text(
                        text = title,
                        style = TextStyles.titleMedium
                    )
                    status.Content()
                }

                action?.let {
                    val activity = LocalActivity.current as? FragmentActivity ?: return@let
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { action.onClick(activity) }) {
                        Text(text = "Request")
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        val content = PermissionItemContent(
            title = "Health Data Permission",
            status = PermissionStatus(granted = true),
            action = PermissionAction(onClick = {})
        )

        content.Content()
    }
}
