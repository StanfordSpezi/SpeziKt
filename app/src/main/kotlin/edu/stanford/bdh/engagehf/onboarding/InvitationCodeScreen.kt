package edu.stanford.bdh.engagehf.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.CommonScaffold
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeView

@Composable
fun InvitationCodeScreen() {
    CommonScaffold(title = stringResource(R.string.invitation_code)) {
        InvitationCodeView()
    }
}
