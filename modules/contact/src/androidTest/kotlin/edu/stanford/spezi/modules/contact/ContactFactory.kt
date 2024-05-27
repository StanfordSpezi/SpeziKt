package edu.stanford.spezi.modules.contact

import androidx.compose.ui.graphics.vector.ImageVector
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOption
import java.util.UUID

object ContactFactory {
    fun create(
        id: UUID = UUID.randomUUID(),
        icon: ImageVector? = null,
        name: String = "",
        title: String = "",
        description: String = "",
        organization: String = "",
        address: String = "",
        options: List<ContactOption> = emptyList()
    ): Contact {
        return Contact(id, icon, name, title, description, organization, address, options)
    }
}
