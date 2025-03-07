package edu.stanford.spezi.spezi.contact.model

import android.content.Context
import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.spezi.core.logging.speziLogger
import edu.stanford.spezi.spezi.foundation.helpers.UUID
import edu.stanford.spezi.spezi.ui.resources.StringResource
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
    val id: UUID = UUID(),
    val image: ImageVector?,
    val title: StringResource,
    val action: (Context) -> Unit,
) {
    companion object {
        internal val logger by speziLogger()
    }
}
