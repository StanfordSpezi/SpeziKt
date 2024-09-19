package edu.stanford.spezi.modules.contact.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.modules.contact.model.ContactOption

/**
 * A card that displays a contact option.
 * @param option The contact option to display.
 * @sample edu.stanford.spezi.modules.contact.component.ContactOptionButtonPreview
 * @see ContactOption
 * @see edu.stanford.spezi.modules.contact.ContactView
 */
@Composable
fun ContactOptionButton(option: ContactOption) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .width(90.dp)
            .clickable {
                option.perform(context)
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
                contentDescription = option.title
            )
            Text(
                text = option.title,
            )
        }
    }
}

@Composable
@Preview
fun ContactOptionButtonPreview() {
    SpeziTheme {
        ContactOptionButton(
            option = ContactOption.email(
                addresses = listOf("test@test.de"),
            ),
        )
    }
}
