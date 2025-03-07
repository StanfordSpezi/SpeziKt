package edu.stanford.spezi.module.onboarding.invitation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.modules.onboarding.R
import edu.stanford.spezi.spezi.ui.helpers.testIdentifier
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors.onPrimary
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors.primary
import edu.stanford.spezi.spezi.ui.helpers.theme.Sizes
import edu.stanford.spezi.spezi.ui.helpers.theme.Spacings
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles.titleLarge

@Composable
fun InvitationCodeView() {
    val viewModel = hiltViewModel<InvitationCodeViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    InvitationCodeView(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun InvitationCodeView(
    uiState: InvitationCodeUiState,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .testIdentifier(InvitationCodeScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.invitation_code),
            modifier = Modifier.testIdentifier(InvitationCodeScreenTestIdentifier.TITLE),
            style = titleLarge,
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Icon(
            imageVector = Icons.Default.Edit,
            tint = primary,
            contentDescription = stringResource(R.string.edit_icon),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(Sizes.Icon.medium)
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Text(
            text = uiState.description,
            modifier = Modifier.testIdentifier(InvitationCodeScreenTestIdentifier.DESCRIPTION)
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        ValidatedOutlinedTextField(
            modifier = Modifier.testIdentifier(InvitationCodeScreenTestIdentifier.INPUT),
            value = uiState.invitationCode,
            onValueChange = {
                onAction(Action.UpdateInvitationCode(it))
                onAction(Action.ClearError)
            },
            labelText = stringResource(R.string.invitation_code),
            errorText = uiState.error,
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Button(
            onClick = {
                onAction(Action.RedeemInvitationCode)
            },
            modifier = Modifier
                .testIdentifier(InvitationCodeScreenTestIdentifier.MAIN_ACTION_BUTTON)
                .fillMaxWidth(),
        ) {
            Text(stringResource(R.string.redeem_invitation_code), color = onPrimary)
        }
    }
}

private class InvitationCodeScreenProvider : PreviewParameterProvider<InvitationCodeUiState> {
    override val values: Sequence<InvitationCodeUiState> = sequenceOf(
        InvitationCodeUiState(
            invitationCode = "",
            error = null,
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            title = "Invitation Code"
        ),
        InvitationCodeUiState(
            invitationCode = "123456",
            error = null,
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            title = "Invitation Code"
        ),
        InvitationCodeUiState(
            invitationCode = "",
            error = "Invalid code",
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            title = "Invitation Code"
        )
    )
}

@Preview
@Composable
private fun InvitationCodeScreenPreview(
    @PreviewParameter(InvitationCodeScreenProvider::class) uiState: InvitationCodeUiState,
) {
    SpeziTheme {
        InvitationCodeView(
            uiState = uiState,
            onAction = { }
        )
    }
}

enum class InvitationCodeScreenTestIdentifier {
    ROOT,
    TITLE,
    DESCRIPTION,
    INPUT,
    MAIN_ACTION_BUTTON,
    SECONDARY_ACTION_BUTTON,
}
