package edu.stanford.spezi.modules.contact.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.modules.contact.OnAction

/**
 * A card that displays an address and allows the user to navigate to it.
 * @param address The address to display and navigate to.
 * @sample edu.stanford.spezi.modules.contact.component.NavigationCardPreview
 * @see edu.stanford.spezi.modules.contact.ContactScreen
 */
@Composable
fun NavigationCard(address: String, publisher: (OnAction) -> Unit) {
    Card(
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
                    publisher(OnAction.NavigateTo(address = address))
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
        NavigationCard("1234 Main Street, 12345 City",
            publisher = { action -> println(action) })
    }
}