package edu.stanford.spezi.spezi.contact.model

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import edu.stanford.spezi.spezi.ui.resources.StringResource

fun ContactOption.Companion.website(uriString: String): ContactOption =
    ContactOption(
        image = Icons.Default.Info,
        title = StringResource("Website"),
        action = { context ->
            runCatching {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                context.startActivity(browserIntent)
            }.onFailure {
                logger.e(it) { "Failed to open intent for website at `$uriString`." }
            }
        }
    )
