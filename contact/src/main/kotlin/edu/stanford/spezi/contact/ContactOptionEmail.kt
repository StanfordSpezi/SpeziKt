package edu.stanford.spezi.contact

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.core.net.toUri
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.StringResource
import java.net.URLEncoder

fun ContactOption.Companion.email(addresses: List<String>, subject: String? = null): ContactOption =
    ContactOption(
        image = Icons.Default.Email,
        title = StringResource(Strings.contact_email),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    val subjectLine = URLEncoder.encode(subject ?: "", "utf-8")
                    val addressLine = URLEncoder.encode(addresses.joinToString(","), "utf-8")
                    data = "mailto:$addressLine?subject=$subjectLine".toUri()
                }
                context.startActivity(intent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for email to `$addresses` with subject `$subject`." }
            }
        }
    )
