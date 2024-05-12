package edu.stanford.spezikt.spezi_module.contact.model

import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

/**
 * Contact data class used to represent a contact.
 *
 * @param id the unique identifier of the contact
 * @param icon the icon of the contact
 * @param name the name of the contact
 * @param title the title of the contact
 * @param description the description of the contact
 * @param organization the organization of the contact
 * @param address the address of the contact
 * @param options the list of contact options
 *
 * @see ContactOption
 * @see ContactOptionType
 *
 */
data class Contact(
    val id: UUID,
    val icon: ImageVector?,
    var name: String,
    val title: String,
    val description: String,
    val organization: String,
    val address: String,
    val options: List<ContactOption>
)

/**
 * ContactOption data class used to represent a contact option.
 *
 * @param id the unique identifier of the contact option
 * @param name the name of the contact option
 * @param value the value of the contact option
 * @param icon the icon of the contact option
 * @param optionType the type of the contact option
 *
 * @see ContactOptionType
 */
data class ContactOption(
    val id: UUID,
    val name: String,
    val value: String,
    val icon: ImageVector?,
    val optionType: ContactOptionType
)

/**
 * ContactOptionType enum class used to represent the type of a contact option.
 *
 * @see ContactOption
 */
enum class ContactOptionType {
    CALL, EMAIL, WEBSITE
}