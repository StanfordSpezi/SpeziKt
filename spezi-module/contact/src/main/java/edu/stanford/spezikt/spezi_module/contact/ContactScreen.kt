package edu.stanford.spezikt.spezi_module.contact


import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme
import edu.stanford.spezikt.spezi_module.contact.component.ContactOptionCard
import edu.stanford.spezikt.spezi_module.contact.component.NavigationCard
import edu.stanford.spezikt.spezi_module.contact.model.Contact
import edu.stanford.spezikt.spezi_module.contact.repository.DefaultContactRepository

/**
 * ContactView composable function to display contact information.
 *
 * @param contact Contact object containing contact information.
 *
 * @sample PrevContactScreen
 *
 * @see Contact
 * @see ContactOptionCard
 * @see NavigationCard
 * @see edu.stanford.spezikt.spezi_module.contact.repository.ContactRepository
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactScreen(contact: Contact) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                contact.icon ?: Icons.Default.AccountBox,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(48.dp)
                            )
                            Column {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Text(
                                    text = contact.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = contact.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            contact.options.forEach { option ->
                                ContactOptionCard(option = option)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        NavigationCard(address = contact.address)
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PrevContactScreen() {
    SpeziKtTheme {
        ContactScreen(DefaultContactRepository().getContact())
    }
}