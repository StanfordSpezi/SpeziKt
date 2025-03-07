package edu.stanford.spezi.spezi.contact

import android.location.Address
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import edu.stanford.spezi.spezi.contact.model.Contact
import edu.stanford.spezi.spezi.contact.model.ContactOption
import edu.stanford.spezi.spezi.contact.model.call
import edu.stanford.spezi.spezi.contact.model.email
import edu.stanford.spezi.spezi.contact.model.text
import edu.stanford.spezi.spezi.contact.model.website
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.ui.resources.ImageResource
import edu.stanford.spezi.spezi.ui.resources.StringResource
import java.util.Locale

object ContactFactory {
    val leland = Contact(
        name = PersonNameComponents(
            givenName = "Leland",
            familyName = "Stanford"
        ),
        image = ImageResource.Vector(Icons.Default.AccountBox, StringResource("Account Box")),
        title = StringResource("University Founder"),
        description = StringResource("""
Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. [...] \
He and his wife Jane were also the founders of Stanford University, which they named after their late son.
[https://en.wikipedia.org/wiki/Leland_Stanford]
"""),
        organization = StringResource("Stanford University"),
        address = Address(Locale.US).apply {
            setAddressLine(0, "450 Serra Mall")
            postalCode = "94305"
            locality = "Stanford"
            adminArea = "CA"
            countryCode = "US"
            countryName = "United States of America"
        },
        options = listOf(
            ContactOption.call("+1 (650) 723-2300"),
            ContactOption.text("+1 (650) 723-2300"),
            ContactOption.email(addresses = listOf("contact@stanford.edu")),
            ContactOption.website("https://stanford.edu")
        )
    )

    val mock = Contact(
        name = PersonNameComponents(
            givenName = "Paul",
            familyName = "Schmiedmayer"
        ),
        image = ImageResource.Vector(Icons.Default.AccountBox, StringResource("Account Box")),
        title = StringResource("A Title"),
        description = StringResource("""
This is a description of a contact that will be displayed. It might even be longer than what has to be displayed in the contact card.
Why is this text so long, how much can you tell about one person?
"""),
        organization = StringResource("Stanford University"),
        address = run {
            val address = Address(Locale.US)
            address.setAddressLine(0, "450 Serra Mall")
            address.postalCode = "94305"
            address.locality = "Stanford"
            address.adminArea = "CA"
            address.countryCode = "US"
            address.countryName = "United States of America"
            address
        },
        options = listOf(
            ContactOption.call("+1 (234) 567-891"),
            ContactOption.text("+1 (234) 567-892"),
            ContactOption.email(addresses = listOf("lelandstanford@stanford.edu"), subject = "Hi Leland!"),
            ContactOption(image = null, title = StringResource("Cloud"), action = {})
        )
    )
}
