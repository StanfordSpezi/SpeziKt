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
    private val action: (context: Context) -> Unit,
) {
    fun perform(context: Context) {
        action(context)
    }

    companion object {
        fun call(number: String): ContactOption {
            return ContactOption(
                image = Icons.Default.Call,
                title = "Call",
                action = { context ->
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${number}")
                    }
                    context.startActivity(dialIntent)
                }
            )
        }

        fun text(number: String): ContactOption {
            return ContactOption(
                image = Icons.Default.Call, // TODO: Find sms icon instead
                title = "Text",
                action = { context ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("sms:${number}")
                    }
                    context.startActivity(intent)
                }
            )
        }

        fun email(addresses: List<String>, subject: String? = null): ContactOption {
            return ContactOption(
                image = Icons.Default.Email,
                title = "Email",
                action = { context ->
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        val subjectLine = URLEncoder.encode(subject ?: "", "utf-8")
                        val addressLine = URLEncoder.encode(addresses.joinToString(","), "utf-8")
                        data = Uri.parse("mailto:${addressLine}?subject=${subjectLine}")
                    }
                    context.startActivity(emailIntent)
                }
            )
        }

        fun website(uriString: String): ContactOption {
            return ContactOption(
                image = Icons.Default.Info,
                title = "Website",
                action = { context ->
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                    context.startActivity(browserIntent)
                }
            )
        }
    }
}