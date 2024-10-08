package edu.stanford.spezi.modules.contact.model

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import edu.stanford.spezi.core.design.component.StringResource

fun ContactOption.Companion.text(number: String): ContactOption =
    ContactOption(
        image = Icons.Default.Call, // TODO: Find sms icon instead
        title = StringResource("Text"),
        action = { context ->
            runCatching {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("sms:$number")
                }
                context.startActivity(intent)
            }.onFailure {
                println("Failed to open intent for text message to `$number` due to `$it`.")
            }
        }
    )
