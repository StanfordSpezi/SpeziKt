package edu.stanford.spezi.modules.contact.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.modules.contact.OnAction
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.ContactOptionType
import java.util.UUID

/**
 * A card that displays a contact option.
 * @param option The contact option to display.
 * @sample edu.stanford.spezi.modules.contact.component.ContactOptionCardPreview
 * @see ContactOption
 * @see ContactOptionType
 * @see edu.stanford.spezi.modules.contact.ContactScreen
 */
@Composable
fun ContactOptionCard(option: ContactOption, publisher: (OnAction) -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .width(90.dp)
            .clickable {
                publisher(OnAction.CardClicked(option = option))
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Spacings.small)
                .fillMaxWidth()
        ) {
            Icon(
                option.icon ?: Icons.Default.Email,
                contentDescription = option.name
            )
            Text(
                text = option.name,
            )
        }
    }
}

@Composable
@Preview
fun ContactOptionCardPreview() {
    SpeziTheme {
        ContactOptionCard(
            option = ContactOption(
                id = UUID.randomUUID(),
                name = "Email",
                value = "test@test.de",
                icon = Icons.Default.Email,
                optionType = ContactOptionType.EMAIL
            ),
            publisher = { action -> println(action) }
        )
    }
}