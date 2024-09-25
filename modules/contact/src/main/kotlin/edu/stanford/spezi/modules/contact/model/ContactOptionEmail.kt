package edu.stanford.spezi.modules.contact.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import java.net.URLEncoder

private data class EmailContactOptionAction(
    private val addresses: List<String>,
    private val subject: String? = null,
): ContactOptionAction {
    override fun handle(context: Context) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            val subjectLine = URLEncoder.encode(subject ?: "", "utf-8")
            val addressLine = URLEncoder.encode(addresses.joinToString(","), "utf-8")
            data = Uri.parse("mailto:$addressLine?subject=$subjectLine")
        }
        context.startActivity(emailIntent)
    }
}

fun ContactOption.Companion.email(addresses: List<String>, subject: String? = null): ContactOption =
    ContactOption(
        image = Icons.Default.Email,
        title = "Email",
        action = EmailContactOptionAction(addresses, subject)
    )
