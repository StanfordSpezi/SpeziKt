package edu.stanford.bdh.engagehf.contact.ui

import android.location.Address
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.design.component.CenteredBoxContent
import edu.stanford.spezi.core.notification.R
import edu.stanford.spezi.spezi.contact.ContactComposable
import edu.stanford.spezi.spezi.contact.model.Contact
import edu.stanford.spezi.spezi.contact.model.ContactOption
import edu.stanford.spezi.spezi.contact.model.call
import edu.stanford.spezi.spezi.contact.model.email
import edu.stanford.spezi.spezi.contact.model.website
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors.primary
import edu.stanford.spezi.spezi.ui.helpers.theme.Spacings
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles
import edu.stanford.spezi.spezi.ui.helpers.theme.ThemePreviews
import edu.stanford.spezi.spezi.ui.resources.ImageResource
import edu.stanford.spezi.spezi.ui.resources.StringResource
import java.util.Locale

@Composable
internal fun ContactScreen() {
    val viewModel = hiltViewModel<ContactScreenViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    ContactScreen(
        onAction = viewModel::onAction,
        uiState = uiState,
    )
}

@Composable
private fun ContactScreen(
    onAction: (ContactScreenViewModel.Action) -> Unit,
    uiState: ContactScreenViewModel.UiState,
) {
    Scaffold(topBar = {
        AppTopAppBar(title = {
            Text(
                text = stringResource(edu.stanford.bdh.engagehf.R.string.contact),
            )
        }, navigationIcon = {
            IconButton(onClick = {
                onAction(ContactScreenViewModel.Action.Back)
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = Spacings.medium)
        ) {
            when (uiState) {
                is ContactScreenViewModel.UiState.Error -> {
                    CenteredBoxContent {
                        Text(
                            text = uiState.message,
                            style = TextStyles.headlineMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                ContactScreenViewModel.UiState.Loading -> {
                    CenteredBoxContent {
                        CircularProgressIndicator(color = primary)
                    }
                }

                is ContactScreenViewModel.UiState.ContactLoaded -> {
                    val contact = uiState.contact
                    ContactComposable(
                        contact = contact,
                    )
                }
            }
        }
    })
}

private class ContactUiStateProvider : PreviewParameterProvider<ContactScreenViewModel.UiState> {
    override val values = sequenceOf(
        ContactScreenViewModel.UiState.Loading,
        ContactScreenViewModel.UiState.Error("An error occurred"),
        ContactScreenViewModel.UiState.ContactLoaded(
            contact = Contact(
                name = PersonNameComponents(
                    givenName = "Leland",
                    familyName = "Stanford"
                ),
                image = ImageResource.Vector(
                    Icons.Default.AccountBox,
                    StringResource(edu.stanford.spezi.spezi.contact.R.string.profile_picture)
                ),
                title = StringResource("University Founder"),
                description = StringResource(
                    """Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician."""
                ),
                organization = StringResource("Stanford University"),
                address = Address(Locale.US).apply {
                    setAddressLine(0, "450 Jane Stanford Way")
                    locality = "Stanford"
                    adminArea = "CA"
                },
                options = listOf(
                    ContactOption.call("+49 123 456 789"),
                    ContactOption.email(listOf("test@gmail.com")),
                    ContactOption.website("https://www.google.com")
                )
            )
        )
    )
}

@ThemePreviews
@Composable
private fun ContactScreenPreview(
    @PreviewParameter(ContactUiStateProvider::class) uiState: ContactScreenViewModel.UiState,
) {
    SpeziTheme {
        ContactScreen(
            onAction = {},
            uiState = uiState
        )
    }
}
