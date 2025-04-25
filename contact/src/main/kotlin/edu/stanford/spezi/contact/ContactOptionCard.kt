package edu.stanford.spezi.contact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews

@Composable
internal fun ContactOptionCard(option: ContactOption, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    DefaultElevatedCard(
        modifier = modifier
            .defaultMinSize(80.dp)
            .fillMaxWidth()
            .clickable {
                option.action(context)
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Spacings.small)
                .fillMaxWidth()
        ) {
            Icon(
                option.image ?: Icons.Default.Email,
                contentDescription = option.title.text(),
                tint = Colors.primary,
            )
            Text(
                text = option.title.text(),
                maxLines = 1,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun ContactOptionCardPreview() {
    SpeziTheme {
        ContactOptionCard(
            ContactOption.email(
                addresses = listOf("test@test.de"),
            ),
        )
    }
}
