package edu.stanford.spezi.modules.contact.component

import android.content.Intent
import android.location.Address
import android.net.Uri
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import edu.stanford.spezi.modules.contact.model.formatted
import java.net.URLEncoder
import java.util.Locale

@Composable
internal fun AddressCard(address: Address) {
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
            val addressText = address.formatted()
            Text(
                text = addressText,
                style = TextStyles.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            val context = LocalContext.current
            IconButton(
                onClick = {
                    val addressQuery = URLEncoder.encode(addressText, "utf-8")
                    val gmmIntentUri = Uri.parse("geo:0,0?q=$addressQuery")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Address",
                )
            }
        }
    }
}

@Composable
@Preview
private fun AddressCardPreview() {
    SpeziTheme {
        AddressCard(run {
            val address = Address(Locale.US)
            address.setAddressLine(0, "1234 Main Street")
            address.postalCode = "12345"
            address.locality = "City"
            address
        })
    }
}
