package edu.stanford.spezi.modules.contact.model

import android.location.Address
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

/**
 * Contact data class used to represent a contact.
 *
 * @param id the unique identifier of the contact
 * @param name the name of the contact
 * @param image the image of the contact
 * @param title the title of the contact
 * @param description the description of the contact
 * @param organization the organization of the contact
 * @param address the address of the contact
 * @param options the list of contact options
 *
 * @see ContactOption
 */
data class Contact(
    val id: UUID = UUID.randomUUID(),
    var name: PersonNameComponents,
    val image: ImageVector?,
    val title: String?,
    val description: String?,
    val organization: String?,
    val address: Address?,
    val options: List<ContactOption>,
)
