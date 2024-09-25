package edu.stanford.spezi.modules.contact.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call

private data class TextContactOptionAction(
    private val number: String,
): ContactOptionAction {
    override fun handle(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("sms:$number")
        }
        context.startActivity(intent)
    }
}

fun ContactOption.Companion.text(number: String) : ContactOption =
    ContactOption(
        image = Icons.Default.Call, // TODO: Find sms icon instead
        title = "Text",
        action = TextContactOptionAction(number)
    )
