package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.AccountSetupState
import edu.stanford.spezi.module.account.account.LocalAccountSetupState
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount

@Composable
fun DefaultAccountSetupHeader() {
    val account = LocalAccount.current
    val accountSetupState = LocalAccountSetupState.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            StringResource("ACCOUNT_WELCOME").text(),
            style = TextStyles.headlineLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Spacings.small, top = 30.dp)
        )

        if (account?.signedIn == true && accountSetupState == AccountSetupState.Generic) {
            Text(
                StringResource("ACCOUNT_WELCOME_SIGNED_IN_SUBTITLE").text(),
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                StringResource("ACCOUNT_WELCOME_SUBTITLE").text(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun DefaultAccountSetupHeaderPreview() {
    SpeziTheme(isPreview = true) {
        DefaultAccountSetupHeader()
    }
}
