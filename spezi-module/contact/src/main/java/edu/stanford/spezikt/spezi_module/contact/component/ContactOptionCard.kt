package edu.stanford.spezikt.spezi_module.contact.component

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme
import edu.stanford.spezikt.spezi_module.contact.model.ContactOption
import edu.stanford.spezikt.spezi_module.contact.model.ContactOptionType
import java.util.UUID

/**
 * A card that displays a contact option.
 * @param option The contact option to display.
 * @sample ContactOptionCardPreview
 * @see ContactOption
 * @see ContactOptionType
 * @see edu.stanford.spezikt.spezi_module.contact.ContactScreen
 */
@Composable
fun ContactOptionCard(option: ContactOption) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .width(90.dp)
            .clickable {
                when (option.optionType) {
                    ContactOptionType.EMAIL -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${option.value}")
                        }
                        context.startActivity(emailIntent)
                    }

                    ContactOptionType.CALL -> {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${option.value}")
                        }
                        context.startActivity(dialIntent)
                    }

                    ContactOptionType.WEBSITE -> {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(option.value))
                        context.startActivity(browserIntent)
                    }
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
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
    SpeziKtTheme {
        ContactOptionCard(
            option = ContactOption(
                id = UUID.randomUUID(),
                name = "Email",
                value = "test@test.de",
                icon = Icons.Default.Email,
                optionType = ContactOptionType.EMAIL
            )
        )
    }
}