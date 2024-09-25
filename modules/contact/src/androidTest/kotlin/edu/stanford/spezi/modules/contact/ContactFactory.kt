package edu.stanford.spezi.modules.contact

import android.content.Context
import android.location.Address
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.ContactOptionAction
import edu.stanford.spezi.modules.contact.model.PersonNameComponents
import edu.stanford.spezi.modules.contact.model.call
import edu.stanford.spezi.modules.contact.model.email
import edu.stanford.spezi.modules.contact.model.text
import edu.stanford.spezi.modules.contact.model.website
import java.util.Locale

private object EmptyContactOptionAction : ContactOptionAction {
    override fun handle(context: Context) {
        println("EmptyContactOptionAction handled.")
    }
}

object ContactFactory {
    val leland = Contact(
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
        image = Icons.Default.AccountBox,
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
            ContactOption(image = null, title = "Cloud", action = EmptyContactOptionAction)
        )
    )
}
