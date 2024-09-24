package edu.stanford.spezi.modules.contact

import android.location.Address
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
import androidx.compose.material3.HorizontalDivider
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
import edu.stanford.spezi.modules.contact.model.PersonNameComponents
import java.util.Locale

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
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacings.small),
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
                                    text = contact.name.formatted(),
                                    style = TextStyles.titleLarge
                                )
                                val subtitle = listOf(contact.title, contact.organization)
                                    .mapNotNull { it }
                                    .joinToString(" - ")
                                if (subtitle.isNotBlank()) {
                                    Text(
                                        text = subtitle,
                                        style = TextStyles.titleSmall
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacings.small))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(Spacings.small))
                        contact.description?.let {
                            Text(
                                text = it,
                                style = TextStyles.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacings.large))
                        FlowRow( // TODO: Fix layout to be more flexible, dynamic and nice
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
                name = PersonNameComponents(givenName = "Leland", familyName = "Stanford"),
                image = Icons.Default.AccountBox,
                title = "University Founder",
                description = """
Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. [...] \
He and his wife Jane were also the founders of Stanford University, which they named after their late son.
[https://en.wikipedia.org/wiki/Leland_Stanford]
""",
                organization = "Stanford University",
                address = run {
                    val address = Address(Locale.US)
                    address.setAddressLine(0, "450 Jane Stanford Way")
                    address.locality = "Stanford"
                    address.adminArea = "CA"
                    address
                },
                options = listOf(
                    ContactOption.call("+49 123 456 789"),
                    ContactOption.email(listOf("test@gmail.com")),
                    ContactOption.website("https://www.google.com")
                )
            )
        )
    }
}
