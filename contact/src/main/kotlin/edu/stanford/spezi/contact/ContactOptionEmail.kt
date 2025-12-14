package edu.stanford.spezi.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import edu.stanford.spezi.ui.StringResource
import java.net.URLEncoder

fun ContactOption.Companion.email(addresses: List<String>, subject: String? = null): ContactOption =
    ContactOption(
        image = Icons.Default.Email,
        title = StringResource(R.string.contact_email),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    val subjectLine = URLEncoder.encode(subject ?: "", "utf-8")
                    val addressLine = URLEncoder.encode(addresses.joinToString(","), "utf-8")
                    data = Uri.parse("mailto:$addressLine?subject=$subjectLine")
                }
                context.startActivity(intent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for email to `$addresses` with subject `$subject`." }
            }
        }
    )
