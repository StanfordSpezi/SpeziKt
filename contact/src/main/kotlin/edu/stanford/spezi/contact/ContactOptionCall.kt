package edu.stanford.spezi.contact

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.core.net.toUri
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.StringResource

fun ContactOption.Companion.call(number: String) =
    ContactOption(
        image = Icons.Default.Call,
        title = StringResource(Strings.contact_call),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$number".toUri()
                }
                context.startActivity(intent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for phone call to `$number`." }
            }
        }
    )
