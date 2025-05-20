package edu.stanford.spezi.contact

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

@Composable
internal fun AddressCard(address: Address, modifier: Modifier = Modifier) {
    DefaultElevatedCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val addressText = remember(address) { address.formatted() }
            Text(
                text = addressText,
                style = TextStyles.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            val context = LocalContext.current
            IconButton(
                onClick = {
                    runCatching {
                        val addressQuery =
                            URLEncoder.encode(addressText, StandardCharsets.UTF_8.toString())
                        val gmmIntentUri = Uri.parse("geo:0,0?q=$addressQuery")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }.onFailure {
                        SpeziLogger.e(it) { "Failed to open intent for address `$addressText`." }
                    }
                },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = stringResource(R.string.contact_address),
                    tint = Colors.primary,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun AddressCardPreview() {
    SpeziTheme {
        AddressCard(Address(Locale.US).apply {
            setAddressLine(0, "1234 Main Street")
            postalCode = "12345"
            locality = "City"
        })
    }
}
