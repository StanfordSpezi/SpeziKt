package edu.stanford.bdh.engagehf.contact.data

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.PersonNameComponents
import edu.stanford.spezi.modules.contact.model.call
import edu.stanford.spezi.modules.contact.model.email
import javax.inject.Inject

class ContactDocumentToContactMapper @Inject constructor() {

    fun map(document: DocumentSnapshot): Result<Contact> = runCatching {
        val (givenName, familyName, title) = document.getString(CONTACT_NAME_FIELD)
            ?.split(", ")
            ?.let {
                it[0].split(" ")
                    .let { nameParts -> Triple(nameParts[0], nameParts[1], it.getOrNull(1)) }
            }
            ?: error("Contact name not found")
        val organisationName =
            document.getString(ORGANISATION_NAME_FIELD) ?: error("Organization name not found")
        val contactEmail =
            document.getString(CONTACT_EMAIL_FIELD)
        val phone = document.getString(CONTACT_PHONE_FIELD)

        Contact(
            name = PersonNameComponents(
                givenName = givenName,
                familyName = familyName,
            ),
            image = null,
            title = title?.let { StringResource(it) },
            description = null,
            organization = StringResource(organisationName),
            address = null,
            options = listOfNotNull(
                phone?.let { ContactOption.call(it) },
                contactEmail?.let { ContactOption.email(listOf(contactEmail)) },
            ),
        )
    }

    private companion object {
        const val CONTACT_NAME_FIELD = "contactName"
        const val CONTACT_EMAIL_FIELD = "emailAddress"
        const val CONTACT_PHONE_FIELD = "phoneNumber"
        const val ORGANISATION_NAME_FIELD = "name"
    }
}
