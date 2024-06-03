package edu.stanford.spezi.modules.contact.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import edu.stanford.spezi.modules.contact.model.ContactOptionType
import java.util.UUID

/**
 *  ContactRepository interface used to define the contact repository.
 */
interface ContactRepository {
    fun getContact(): Contact
}

/**
 * DefaultContactRepository class used to provide the default contact repository.
 */
class DefaultContactRepository : ContactRepository {
    override fun getContact(): Contact {
        return Contact(
            id = UUID.randomUUID(),
            icon = null,
            name = "Leland Stanford",
            title = "CEO",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            organization = "Stanford University",
            address = "450 Jane Stanford Way Stanford, CA",
            options = listOf(
                ContactOption(
                    UUID.randomUUID(),
                    "Call",
                    "+49 123 456 789",
                    Icons.Default.Call,
                    ContactOptionType.CALL
                ),
                ContactOption(
                    UUID.randomUUID(),
                    "Email",
                    "test@gmail.com",
                    Icons.Default.Email,
                    ContactOptionType.EMAIL
                ),
                ContactOption(
                    UUID.randomUUID(),
                    "Website",
                    "https://www.google.com",
                    Icons.Default.Info,
                    ContactOptionType.WEBSITE
                )
            )
        )
    }
}
