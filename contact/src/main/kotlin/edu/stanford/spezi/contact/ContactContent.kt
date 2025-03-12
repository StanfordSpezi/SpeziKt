package edu.stanford.spezi.contact

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents
import edu.stanford.spezi.ui.testing.testIdentifier
import java.util.Locale

/**
 * ContactView composable function to display contact information.
 *
 * @param modifier A modifier to modify the inner composable.
 *
 * @sample ContactContentPreview
 *
 * @see Contact
 * @see ContactOptionCard
 * @see AddressCard
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Contact.Content(modifier: Modifier = Modifier) {
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
                image.Content(
                    modifier = Modifier
                        .size(Sizes.Icon.medium)
                )
                Column {
                    Text(
                        text = remember(name) { name.formatted() },
                        style = TextStyles.titleLarge,
                        modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.NAME)
                    )
                    val context = LocalContext.current
                    val subtitle = remember(title, organization) {
                        listOf(title, organization)
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
            description?.let {
                Text(
                    text = it.text(),
                    style = TextStyles.bodyMedium,
                    modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.DESCRIPTION)
                )
            }
            Spacer(modifier = Modifier.height(Spacings.large))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacings.small),
                verticalArrangement = Arrangement.spacedBy(Spacings.small)
            ) {
                options.forEach { option ->
                    option.Content(
                        modifier = Modifier
                            .weight(1f)
                            .testIdentifier(
                                ContactComposableTestIdentifier.OPTION,
                                suffix = option.title.text(),
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacings.small))
            address?.let { address ->
                AddressCard(
                    address = address,
                    modifier = Modifier.testIdentifier(ContactComposableTestIdentifier.ADDRESS)
                )
            }
        }
    }
}

enum class ContactComposableTestIdentifier {
    NAME, SUBTITLE, DESCRIPTION, ADDRESS, OPTION
}

@ThemePreviews
@Composable
private fun ContactContentPreview(@PreviewParameter(ContactProvider::class) contact: Contact) {
    SpeziTheme(isPreview = true) {
        contact.Content(Modifier)
    }
}

private class ContactProvider : PreviewParameterProvider<Contact> {
    override val values: Sequence<Contact> = sequenceOf(
        ContactContentFactory.create(),
        ContactContentFactory.create(
            options = listOf(
                ContactOption.call("+49 123 456 789"),
                ContactOption.email(listOf("test@gmail.com")),
                ContactOption.website("https://www.google.com"),
                ContactOption.text("+49 123 456 789"),
            ),
        ),
        ContactContentFactory.create(
            options = listOf(
                ContactOption.call("+49 123 456 789"),
                ContactOption.email(listOf("test@gmail.com")),
                ContactOption.website("https://www.google.com"),
                ContactOption.text("+49 123 456 789"),
                ContactOption.text("+49 123 456 789"),
            ),
        )
    )
}

private object ContactContentFactory {
    fun create(
        title: StringResource = StringResource("University Founder"),
        description: StringResource = StringResource(
            """Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. 
                He and his wife Jane were also the founders of Stanford University, which they named after their late son.
                """
        ),
        address: Address = Address(Locale.US).apply {
            setAddressLine(0, "450 Jane Stanford Way")
            locality = "Stanford"
            adminArea = "CA"
        },
        options: List<ContactOption> = listOf(
            ContactOption.call("+49 123 456 789"),
            ContactOption.email(listOf("test@gmail.com")),
            ContactOption.website("https://www.test.test")
        ),
    ): Contact {
        return Contact(
            name = PersonNameComponents(
                givenName = "Leland",
                familyName = "Stanford"
            ),
            image = ImageResource.Vector(Icons.Default.AccountBox, StringResource(R.string.profile_picture)),
            title = title,
            description = description,
            organization = StringResource("Stanford University"),
            address = address,
            options = options
        )
    }
}
