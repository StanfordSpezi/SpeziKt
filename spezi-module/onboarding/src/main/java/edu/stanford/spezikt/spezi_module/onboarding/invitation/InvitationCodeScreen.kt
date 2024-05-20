package edu.stanford.spezikt.spezi_module.onboarding.invitation

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
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezikt.core.design.component.SpeziValidatedOutlinedTextField
import edu.stanford.spezikt.core.design.theme.Colors.onPrimary
import edu.stanford.spezikt.core.design.theme.Colors.primary
import edu.stanford.spezikt.core.design.theme.Sizes
import edu.stanford.spezikt.core.design.theme.Spacings
import edu.stanford.spezikt.core.design.theme.TextStyles.titleLarge

@Composable
fun InvitationCodeScreen(
) {
    val viewModel = hiltViewModel<InvitationCodeViewModel>()
    val uiState by viewModel.uiState.collectAsState()

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
                .size(Sizes.icon)
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Text("Please enter your invitation code to join the ENGAGE-HF study.")
        Spacer(modifier = Modifier.height(Spacings.medium))
        SpeziValidatedOutlinedTextField(
            value = uiState.invitationCode,
            onValueChange = {
                viewModel.onAction(
                    Action.UpdateInvitationCode(
                        it,
                        TextFieldType.INVITATION_CODE
                    )
                )
                viewModel.onAction(Action.ClearError)
            },
            labelText = "Invitation Code",
            errorText = uiState.error,
            isValid = uiState.error == null
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        Button(
            onClick = {
                viewModel.redeemInvitationCode()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Redeem Invitation Code", color = onPrimary)
        }
        Spacer(modifier = Modifier.height(Spacings.small))
        TextButton(onClick = {
            // TODO navigate to login screen
        }) {
            Text("I Already Have an Account")
        }
    }
}