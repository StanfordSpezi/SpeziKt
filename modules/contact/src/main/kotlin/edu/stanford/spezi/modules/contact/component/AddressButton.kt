package edu.stanford.spezi.modules.contact.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles

/**
 * A card that displays an address and allows the user to navigate to it.
 * @param address The address to display and navigate to.
 * @sample edu.stanford.spezi.modules.contact.component.NavigationCardPreview
 * @see edu.stanford.spezi.modules.contact.ContactView
 */
@Composable
fun AddressButton(address: String) {
    val context = LocalContext.current
    DefaultElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Place,
                contentDescription = "Address",
            )
            Text(
                text = address,
                style = TextStyles.bodyMedium,
                modifier = Modifier.weight(1f)
            )
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
    SpeziTheme {
        AddressButton("1234 Main Street, 12345 City")
    }
}
