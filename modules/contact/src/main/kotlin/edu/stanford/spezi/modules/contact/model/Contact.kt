package edu.stanford.spezi.modules.contact.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import java.net.URLEncoder
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
    var name: String,
    val image: ImageVector?,
    val title: String?,
    val description: String?,
    val organization: String?,
    val address: String?,
    val options: List<ContactOption>,
)
