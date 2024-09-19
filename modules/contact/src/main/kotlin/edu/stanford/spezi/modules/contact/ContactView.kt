package edu.stanford.spezi.modules.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.modules.contact.component.AddressButton
import edu.stanford.spezi.modules.contact.component.ContactOptionButton
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption

/**
 * ContactView composable function to display contact information.
 *
 * @param contact The contact associated with the view.
 *
 * @sample edu.stanford.spezi.modules.contact.ContactViewPreview
 *
 * @see Contact
 * @see ContactOptionButton
 * @see AddressButton
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactView(contact: Contact) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium),
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacings.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(Spacings.medium))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacings.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacings.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                contact.image ?: Icons.Default.AccountBox,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(Sizes.Icon.medium)
                            )
                            Column {
                                Text(
                                    text = contact.name,
                                    style = TextStyles.titleLarge
                                )
                                contact.title?.let {
                                    Text(
                                        text = it,
                                        style = TextStyles.titleSmall
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacings.small))
                        contact.description?.let {
                            Text(
                                text = it,
                                style = TextStyles.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacings.large))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            contact.options.forEach { option ->
                                ContactOptionButton(option = option)
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacings.large))
                        contact.address?.let { address ->
                            AddressButton(address = address)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ContactViewPreview() {
    SpeziTheme {
        ContactView(
            Contact(
                name = "Leland Stanford",
                image = null,
                title = "CEO",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                organization = "Stanford University",
                address = "450 Jane Stanford Way Stanford, CA",
                options = listOf(
                    ContactOption.call("+49 123 456 789"),
                    ContactOption.email(listOf("test@gmail.com")),
                    ContactOption.website("https://www.google.com")
                )
            )
        )
    }
}
