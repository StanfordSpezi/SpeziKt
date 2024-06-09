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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge

@Composable
fun InvitationCodeScreen() {
    val viewModel = hiltViewModel<InvitationCodeViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    InvitationCodeScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun InvitationCodeScreen(
    uiState: InvitationCodeUiState,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Invitation Code",
            style = titleLarge,
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Icon(
            imageVector = Icons.Default.Edit,
            tint = primary,
            contentDescription = "Edit Icon",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(Sizes.Icon.medium)
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Text(uiState.description)
        Spacer(modifier = Modifier.height(Spacings.medium))
        ValidatedOutlinedTextField(
            value = uiState.invitationCode,
            onValueChange = {
                onAction(Action.UpdateInvitationCode(it))
                onAction(Action.ClearError)
            },
            labelText = "Invitation Code",
            errorText = uiState.error,
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Button(
            onClick = {
                onAction(Action.RedeemInvitationCode)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Redeem Invitation Code", color = onPrimary)
        }
        Spacer(modifier = Modifier.height(Spacings.small))
        TextButton(onClick = {
            onAction(Action.AlreadyHasAccountPressed)
        }) {
            Text("I Already Have an Account")
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
    InvitationCodeScreen(
        uiState = uiState,
        onAction = { }
    )
}
