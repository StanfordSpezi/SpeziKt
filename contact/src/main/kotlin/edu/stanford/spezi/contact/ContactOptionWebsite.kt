package edu.stanford.spezi.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import edu.stanford.spezi.ui.StringResource

fun ContactOption.Companion.website(uriString: String): ContactOption =
    ContactOption(
        image = Icons.Default.Info,
        title = StringResource(R.string.contact_website),
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
