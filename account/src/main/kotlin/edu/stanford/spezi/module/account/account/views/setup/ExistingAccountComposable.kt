package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendTextButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.views.AccountSummaryBox

@Composable
internal fun ExistingAccountComposable(
    details: AccountDetails,
    modifier: Modifier = Modifier,
    continueContent: @Composable () -> Unit = {},
) {
    val account = LocalAccount.current
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    ViewStateAlert(viewState)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.defaultMinSize(minHeight = 180.dp))
        AccountSummaryBox(details)
        Spacer(Modifier.defaultMinSize(minHeight = 180.dp))
        continueContent()
        SuspendTextButton(
            title = StringResource("UP_LOGOUT").text(),
            state = viewState,
            modifier = Modifier.padding(Spacings.small),
            colors = ButtonDefaults.textButtonColors().copy(contentColor = Color.Red),
        ) {
            account?.accountService?.logout()
        }
        Spacer(Modifier.height(20.dp))
    }
}

@ThemePreviews
@Composable
private fun ExistingAccountComposablePreview() {
    val details = remember {
        AccountDetails().apply {
            userId = "leland@stanford.edu"
            name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        }
    }
    SpeziTheme(isPreview = true) {
        ExistingAccountComposable(details, Modifier.padding(horizontal = Spacings.small)) {
            Button(onClick = {
                println("Pressed")
            }) {
                Text(
                    "Continue",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacings.small),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
