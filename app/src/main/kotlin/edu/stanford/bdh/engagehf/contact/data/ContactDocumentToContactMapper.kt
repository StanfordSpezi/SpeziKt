package edu.stanford.bdh.engagehf.contact.data

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.validation.personalInfo.PersonNameComponents
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.call
import edu.stanford.spezi.modules.contact.model.email
import javax.inject.Inject

class ContactDocumentToContactMapper @Inject constructor() {

    fun map(document: DocumentSnapshot): Result<Contact> = runCatching {
        val contactName = document.getString(CONTACT_NAME_FIELD)
        val organisationName = document.getString(ORGANISATION_NAME_FIELD)
        if (contactName == null || organisationName == null) {
            error("Missing required data, contactName: $contactName, organisation: $organisationName")
        }
        val components = contactName.split(", ")
        val nameComponents = components.firstOrNull()?.split(" ")
        val personNameComponents = PersonNameComponents(
            givenName = nameComponents?.getOrNull(0),
            familyName = nameComponents?.drop(1)
                ?.joinToString(" ") // assigning everything besides given name here
        )
        val title = components.lastOrNull()
        val contactEmail = document.getString(CONTACT_EMAIL_FIELD)
        val phone = document.getString(CONTACT_PHONE_FIELD)
        Contact(
            name = personNameComponents,
            title = title?.let { StringResource(it) },
            organization = StringResource(organisationName),
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
