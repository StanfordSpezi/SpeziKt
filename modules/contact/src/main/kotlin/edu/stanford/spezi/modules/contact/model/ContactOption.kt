package edu.stanford.spezi.modules.contact.model

import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

/**
 * ContactOption data class used to represent a contact option.
 *
 * @param id the unique identifier of the contact option
 * @param image the image of the contact option
 * @param title the title of the contact option
 * @param action the action of the contact option
 */
data class ContactOption(
    val id: UUID = UUID.randomUUID(),
    val image: ImageVector?,
    val title: String,
    val action: ContactOptionAction,
) {
    companion object
}
