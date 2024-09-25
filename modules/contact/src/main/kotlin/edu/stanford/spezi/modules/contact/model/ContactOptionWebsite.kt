package edu.stanford.spezi.modules.contact.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info

private data class WebsiteContactOptionAction(
    private val uriString: String
): ContactOptionAction {
    override fun handle(context: Context) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
        context.startActivity(browserIntent)
    }
}

fun ContactOption.Companion.website(uriString: String): ContactOption =
    ContactOption(
        image = Icons.Default.Info,
        title = "Website",
        action = WebsiteContactOptionAction(uriString)
    )