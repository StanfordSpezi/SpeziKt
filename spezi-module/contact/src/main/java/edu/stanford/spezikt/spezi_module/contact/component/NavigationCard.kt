package edu.stanford.spezikt.spezi_module.contact.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme

/**
 * A card that displays an address and allows the user to navigate to it.
 * @param address The address to display and navigate to.
 * @sample NavigationCardPreview
 * @see edu.stanford.spezikt.spezi_module.contact.ContactScreen
 */
@Composable
fun NavigationCard(address: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Place,
                contentDescription = "Address",
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            val context = LocalContext.current
            IconButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=$address")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Navigate to"
                )
            }
        }
    }
}

@Composable
@Preview
fun NavigationCardPreview() {
    SpeziKtTheme {
        NavigationCard("1234 Main Street, 12345 City")
    }
}