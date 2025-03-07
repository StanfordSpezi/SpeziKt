package edu.stanford.spezi.spezi.contact.model

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import edu.stanford.spezi.spezi.ui.resources.StringResource

fun ContactOption.Companion.call(number: String) =
    ContactOption(
        image = Icons.Default.Call,
        title = StringResource("Call"),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                }
                context.startActivity(intent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for phone call to `$number`." }
            }
        }
    )
