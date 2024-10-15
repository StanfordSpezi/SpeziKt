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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.modules.contact.component.AddressCard
import edu.stanford.spezi.modules.contact.component.ContactOptionCard
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.PersonNameComponents
import edu.stanford.spezi.modules.contact.model.call
import edu.stanford.spezi.modules.contact.model.email
import edu.stanford.spezi.modules.contact.model.website
import java.util.Locale

/**
 * ContactView composable function to display contact information.
 *
 * @param contact The contact associated with the view.
 *
 * @sample edu.stanford.spezi.modules.contact.ContactComposablePreview
 *
 * @see Contact
 * @see ContactOptionCard
 * @see AddressCard
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactComposable(contact: Contact, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
                val image = contact.image ?: Icons.Default.AccountBox
                Icon(
                    image,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(Sizes.Icon.medium)
                        .testIdentifier(ContactComposableTestIdentifier.IMAGE, image.name)
                )
                Column {
                    Text(
                        text = remember(contact.name) { contact.name.formatted() },
                        style = TextStyles.titleLarge,
                        modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.NAME)
                    )
                    val context = LocalContext.current
                    val subtitle = remember(contact.title, contact.organization) {
                        listOf(contact.title, contact.organization)
                            .mapNotNull { it?.get(context) }
                            .joinToString(" - ")
                    }
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = TextStyles.titleSmall,
                            modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.SUBTITLE)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacings.small))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacings.small))
            contact.description?.let {
                Text(
                    text = it.text(),
                    style = TextStyles.bodyMedium,
                    modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.DESCRIPTION)
                )
            }
            Spacer(modifier = Modifier.height(Spacings.large))
            FlowRow( // TODO: Fix layout to be more flexible, dynamic and nice
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                contact.options.forEach { option ->
                    ContactOptionCard(
                        option = option,
                        modifier = Modifier.testIdentifier(
                            ContactComposableTestIdentifier.OPTION,
                            suffix = option.title.text(),
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacings.large))
            contact.address?.let { address ->
                AddressCard(
                    address = address,
                    modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.ADDRESS)
                )
            }
        }
    }
}

enum class ContactComposableTestIdentifier {
    NAME, IMAGE, SUBTITLE, DESCRIPTION, ADDRESS, OPTION
}

@ThemePreviews
@Composable
private fun ContactComposablePreview() {
    SpeziTheme(isPreview = true) {
        ContactComposable(
            Contact(
                name = PersonNameComponents(givenName = "Leland", familyName = "Stanford"),
                image = Icons.Default.AccountBox,
                title = StringResource("University Founder"),
                description = StringResource("""
Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. [...] \
He and his wife Jane were also the founders of Stanford University, which they named after their late son.
[https://en.wikipedia.org/wiki/Leland_Stanford]
"""),
                organization = StringResource("Stanford University"),
                address = Address(Locale.US).apply {
                    setAddressLine(0, "450 Jane Stanford Way")
                    locality = "Stanford"
                    adminArea = "CA"
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
