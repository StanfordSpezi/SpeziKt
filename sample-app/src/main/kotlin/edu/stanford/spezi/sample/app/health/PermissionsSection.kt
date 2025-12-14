package edu.stanford.spezi.sample.app.health

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class PermissionsSection(
    val title: String,
    val action: PermissionAction?,
    val items: List<PermissionItemContent>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.small)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = TextStyles.headlineSmall
                )

                action?.let {
                    val activity = LocalActivity.current as? FragmentActivity ?: return@let
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { it.onClick(activity) }) {
                        Text(text = "Request All")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacings.small)) {
                items.forEach { item ->
                    item.Content()
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        val content = PermissionsSection(
            title = "Permissions",
            action = null,
            items = listOf(
                PermissionItemContent(
                    title = "Health Data Permission",
                    status = PermissionStatus(granted = true),
                    action = null
                ),
                PermissionItemContent(
                    title = "Activity Data Permission",
                    status = PermissionStatus(granted = false),
                    action = PermissionAction(onClick = {})
                )
            )
        )
        content.Content()
    }
}
