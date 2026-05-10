package edu.stanford.spezi.contact

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.core.net.toUri
import edu.stanford.spezi.ui.StringResource

fun ContactOption.Companion.text(number: String): ContactOption =
    ContactOption(
        image = Icons.AutoMirrored.Default.Send,
        title = StringResource(R.string.contact_text),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "sms:$number".toUri()
                }
                context.startActivity(intent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for text message to `$number`." }
            }
        }
    )
