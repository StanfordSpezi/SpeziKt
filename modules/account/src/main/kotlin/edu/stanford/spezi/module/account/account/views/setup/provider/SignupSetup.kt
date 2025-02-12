package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.model.UserIdPasswordCredential

@Composable
internal fun SignupSetup(
    style: MutableState<PresentedSetupStyle>,
    login: (suspend (UserIdPasswordCredential) -> Unit)?,
    presentingSetup: MutableState<Boolean>,
) {
    Column {
        AccountServiceButton(
            StringResource("UP_SIGNUP").text(),
            Modifier.padding(bottom = 12.dp)
        ) {
            presentingSetup.value = true
        }

        login?.let { loginBlock ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    "Already got an Account?",
                    style = TextStyles.bodyMedium,
                )
                Text(
                    " ",
                    style = TextStyles.bodyMedium,
                )
                Text(
                    "Login",
                    style = TextStyles.bodyMedium,
                    modifier = Modifier.clickable { style.value = PresentedSetupStyle.Login(loginBlock) }
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SignupSetupPreview() {
    val style = remember { mutableStateOf<PresentedSetupStyle>(PresentedSetupStyle.SignUp) }
    val presentingSetup = remember { mutableStateOf(false) }

    SpeziTheme(isPreview = true) {
        SignupSetup(
            style,
            login = { },
            presentingSetup
        )
    }
}
