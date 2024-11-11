package edu.stanford.spezi.modules.contact.model

import android.location.Address
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
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
    val image: ImageResource = ImageResource.Vector(Icons.Default.AccountBox),
    val title: StringResource? = null,
    val description: StringResource? = null,
    val organization: StringResource? = null,
    val address: Address? = null,
    val options: List<ContactOption>,
)
