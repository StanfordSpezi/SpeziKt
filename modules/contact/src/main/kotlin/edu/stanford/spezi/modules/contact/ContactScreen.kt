package edu.stanford.spezi.modules.contact

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.modules.contact.component.ContactOptionCard
import edu.stanford.spezi.modules.contact.component.NavigationCard
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.repository.DefaultContactRepository

/**
 * ContactView composable function to display contact information.
 *
 * @param viewModel The ViewModel associated with the screen.
 *
 * @sample edu.stanford.spezi.modules.contact.ContactScreenPreview
 *
 * @see Contact
 * @see ContactOptionCard
 * @see NavigationCard
 * @see edu.stanford.spezi.modules.contact.repository.ContactRepository
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactScreen(viewModel: ContactViewModel) {
    val contact by viewModel.contact.collectAsState()

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium),
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacings.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(Spacings.medium))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacings.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacings.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                contact?.icon ?: Icons.Default.AccountBox,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(Sizes.iconMedium)
                            )
                            Column {
                                contact?.let {
                                    Text(
                                        text = it.name,
                                        style = TextStyles.titleLarge
                                    )
                                }
                                contact?.let {
                                    Text(
                                        text = it.title,
                                        style = TextStyles.titleSmall
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacings.small))
                        contact?.let {
                            Text(
                                text = it.description,
                                style = TextStyles.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacings.large))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val context = LocalContext.current
                            contact?.options?.forEach { option ->
                                ContactOptionCard(option = option) {
                                    viewModel.handleAction(it, context)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacings.large))
                        val context = LocalContext.current
                        contact?.let { contact ->
                            NavigationCard(address = contact.address) {
                                viewModel.handleAction(it, context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ContactScreenPreview(
    @PreviewParameter(ContactViewModelPreviewParameterProvider::class) viewModel: ContactViewModel,
) {
    SpeziTheme {
        ContactScreen(viewModel)
    }
}

class ContactViewModelPreviewParameterProvider : PreviewParameterProvider<ContactViewModel> {
    override val values: Sequence<ContactViewModel> = sequenceOf(
        ContactViewModel(DefaultContactRepository()),
    )
}
