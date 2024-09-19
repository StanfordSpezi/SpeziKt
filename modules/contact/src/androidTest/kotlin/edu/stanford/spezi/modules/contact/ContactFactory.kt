package edu.stanford.spezi.modules.contact

import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import java.util.UUID

object ContactFactory {
    fun create(
        id: UUID = UUID.randomUUID(),
        name: String = "",
        image: ImageVector? = null,
        title: String = "",
        description: String = "",
        organization: String = "",
        address: String = "",
        options: List<ContactOption> = emptyList(),
    ): Contact {
        return Contact(
            id = id,
            name = name,
            image = image,
            title = title,
            description = description,
            organization = organization,
            address = address,
            options = options,
        )
    }
}
