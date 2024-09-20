package edu.stanford.spezi.modules.contact

import android.location.Address
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.PersonNameComponents
import java.util.Locale

object ContactFactory {
    val leland = Contact(
        name = PersonNameComponents(givenName = "Leland", familyName = "Stanford"),
        image = null, // Image(systemName: "figure.wave.circle"),
        title = "University Founder",
        description = """
                      Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. [...] \
                      He and his wife Jane were also the founders of Stanford University, which they named after their late son.
                      [https://en.wikipedia.org/wiki/Leland_Stanford]
                      """,
        organization = "Stanford University",
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
            ContactOption.call("+1 (650) 723-2300"),
            ContactOption.text("+1 (650) 723-2300"),
            ContactOption.email(addresses = listOf("contact@stanford.edu")),
            ContactOption.website("https://stanford.edu")
        )
    )

    val mock = Contact(
        name = PersonNameComponents(givenName = "Paul", familyName = "Schmiedmayer"),
        image = null,  // Image(systemName: "figure.wave.circle"), // swiftlint:disable:this accessibility_label_for_image
        title = "A Title",
        description = """
                      This is a description of a contact that will be displayed. It might even be longer than what has to be displayed in the contact card.
                      Why is this text so long, how much can you tell about one person?
                      """,
        organization = "Stanford University",
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
            ContactOption(image = null, title = "Cloud", action = { })
        )
    )
}
