package edu.stanford.spezikt.spezi_module.contact.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import edu.stanford.spezikt.spezi_module.contact.model.Contact
import edu.stanford.spezikt.spezi_module.contact.model.ContactOption
import edu.stanford.spezikt.spezi_module.contact.model.ContactOptionType
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
            UUID.randomUUID(),
            null,
            "Leland Stanford",
            "CEO",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Stanford University",
            "450 Jane Stanford Way Stanford, CA",
            listOf(
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