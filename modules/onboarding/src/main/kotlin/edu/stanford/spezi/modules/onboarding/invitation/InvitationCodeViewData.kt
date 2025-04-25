package edu.stanford.spezi.modules.onboarding.invitation

import edu.stanford.spezi.ui.StringResource

data class InvitationCodeViewData(
    val description: StringResource,
    val redeemAction: () -> Unit,
)
