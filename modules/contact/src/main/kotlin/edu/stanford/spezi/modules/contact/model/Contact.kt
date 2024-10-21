package edu.stanford.spezi.modules.contact.model

import android.location.Address
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.core.design.component.StringResource
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
    val name: PersonNameComponents,
    val image: ImageVector? = null,
    val title: StringResource? = null,
    val description: StringResource? = null,
    val organization: StringResource? = null,
    val address: Address? = null,
    val options: List<ContactOption>,
)
