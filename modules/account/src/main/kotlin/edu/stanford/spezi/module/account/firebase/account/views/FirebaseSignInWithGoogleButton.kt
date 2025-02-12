package edu.stanford.spezi.module.account.firebase.account.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.firebase.account.FirebaseAccountService

@Composable
internal fun FirebaseSignInWithGoogleButton(
    service: FirebaseAccountService,
) {
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    ViewStateAlert(viewState)

    SuspendButton(
        onClick = {
        },
        state = viewState,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(edu.stanford.spezi.module.account.R.string.google_icon),
            modifier = Modifier.size(Sizes.Icon.small)
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text(stringResource(edu.stanford.spezi.module.account.R.string.sign_in_with_google))
    }
}
