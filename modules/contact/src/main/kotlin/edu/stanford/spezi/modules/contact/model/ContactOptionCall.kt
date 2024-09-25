package edu.stanford.spezi.modules.contact.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call

private data class CallContactOptionAction(
    private val number: String,
): ContactOptionAction {
    override fun handle(context: Context) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        context.startActivity(dialIntent)
    }
}

fun ContactOption.Companion.call(number: String) =
    ContactOption(
        image = Icons.Default.Call,
        title = "Call",
        action = CallContactOptionAction(number)
    )
