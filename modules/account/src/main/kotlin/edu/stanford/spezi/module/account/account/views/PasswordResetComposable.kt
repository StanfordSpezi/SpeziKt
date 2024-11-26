package edu.stanford.spezi.module.account.account.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.service.configuration.UserIdConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.userIdConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PasswordReset(
    success: @Composable () -> Unit = { SuccessfulPasswordResetComposable() },
    resetPassword: suspend (String) -> Unit,
) {
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val userId = remember { mutableStateOf("") }
    val requestSubmitted = remember { mutableStateOf(false) }
    val validation = remember { mutableStateOf(ValidationContext()) }
    val requestSubmittedProgress by animateFloatAsState(if (requestSubmitted.value) 0f else 1f,
        label = "requestSubmittedProgress"
    )

    LazyColumn {
        item {
            if (requestSubmitted.value) {
                Box(Modifier.alpha(1 - requestSubmittedProgress)) {
                    success()
                }
            } else {
                Box(Modifier.alpha(requestSubmittedProgress)) {
                    ReceiveValidation(validation) {
                        PasswordResetForm(viewState, userId, validation.value, requestSubmitted, resetPassword)
                    }
                    Spacer(Modifier.fillMaxHeight())
                }
            }
        }
    }
}

@Composable
private fun PasswordResetForm(
    state: MutableState<ViewState>,
    userId: MutableState<String>,
    validation: ValidationContext,
    requestSubmitted: MutableState<Boolean>,
    resetPassword: suspend (String) -> Unit,
) {
    val account = LocalAccount.current
    val userIdConfiguration = remember {
        account?.accountService?.configuration?.userIdConfiguration ?: UserIdConfiguration.emailAddress
    }
    val idTypeStringResource = userIdConfiguration.idType.stringResource
    val asyncScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            StringResource(
                "UAP_PASSWORD_RESET_SUBTITLE ${idTypeStringResource.text()}"
            ).text(),
            modifier = Modifier.padding(8.dp).padding(bottom = 30.dp)
        )

        Validate(userId.value, rule = ValidationRule.nonEmpty) {
            VerifiableTextField(
                idTypeStringResource.text(),
                userId
            )
        }

        SuspendButton(
            state = state,
            onClick = {
                if (!validation.validateHierarchy()) return@SuspendButton
                resetPassword(userId.value)
                asyncScope.launch {
                    delay(515.milliseconds)
                    state.value = ViewState.Idle
                }
                requestSubmitted.value = true
            },
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                StringResource("UP_RESET_PASSWORD").text(),
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
    // .environment(\.defaultErrorDescription, .init("UAP_RESET_PASSWORD_FAILED_DEFAULT_ERROR", bundle: .atURL(from: .module)))
}

@ThemePreviews
@Composable
private fun PasswordResetComposablePreview() {
    SpeziTheme(isPreview = true) {
        PasswordReset {
            println("Reset password for $it")
        }
    }
}
