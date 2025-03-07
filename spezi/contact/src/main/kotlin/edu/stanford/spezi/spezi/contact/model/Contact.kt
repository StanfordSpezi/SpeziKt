package edu.stanford.spezi.spezi.contact.model

import android.location.Address
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import edu.stanford.spezi.spezi.contact.R
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.ui.resources.ImageResource
import edu.stanford.spezi.spezi.ui.resources.StringResource
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
    val image: ImageResource = ImageResource.Vector(Icons.Default.AccountBox, StringResource(R.string.profile_picture)),
    val title: StringResource? = null,
    val description: StringResource? = null,
    val organization: StringResource? = null,
    val address: Address? = null,
    val options: List<ContactOption>,
)
