package edu.stanford.spezi.modules.contact.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.email

@Composable
internal fun ContactOptionCard(option: ContactOption, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    DefaultElevatedCard(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .width(90.dp)
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
                contentDescription = option.title.text()
            )
            Text(option.title.text())
        }
    }
}

@Composable
@ThemePreviews
private fun ContactOptionCardPreview() {
    SpeziTheme(isPreview = true) {
        ContactOptionCard(
            ContactOption.email(
                addresses = listOf("test@test.de"),
            ),
        )
    }
}
