package edu.stanford.spezi.module.account.account.views.signup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.compositionLocal.AccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountServiceConfiguration
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.ReportSignupProviderCompliance
import edu.stanford.spezi.module.account.account.compositionLocal.SignupProviderCompliance
import edu.stanford.spezi.module.account.account.service.configuration.requiredAccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import java.util.EnumSet

@Composable
fun SignupForm(
    header: @Composable () -> Unit = { SignupFormHeader() },
    signup: suspend (AccountDetails) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val account = LocalAccount.current
    val signupDetails = remember { AccountDetails() }

    val compliance = remember { mutableStateOf<SignupProviderCompliance?>(null) }

    ReportSignupProviderCompliance(compliance.value)

    SignupFormContent(header, signup, onDismissRequest)

// TODO: .disableDismissiveActions(isProcessing: viewState)
// .interactiveDismissDisabled(!signupDetailsBuilder.isEmpty)

        /*
        .confirmationDialog(
            Text("CONFIRMATION_DISCARD_INPUT_TITLE", bundle: .module),
    isPresented: $presentingCloseConfirmation,
    titleVisibility: .visible
    ) {
        Button(role: .destructive, action: {
        dismiss()
    }) {
        Text("CONFIRMATION_DISCARD_INPUT", bundle: .module)
    }
        Button(role: .cancel, action: {}) {
        Text("CONFIRMATION_KEEP_EDITING", bundle: .module)
    }
    }
            .toolbar {
            ToolbarItem(placement: .cancellationAction) {
            Button(action: {
                if signupDetailsBuilder.isEmpty {
                    dismiss()
                } else {
                    presentingCloseConfirmation = true
                }
            }) {
            Text("CLOSE", bundle: .module)
        }
        }
        }

         */
}

@Composable
private fun SignupFormContent(
    header: @Composable () -> Unit,
    signup: suspend (AccountDetails) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val account = LocalAccount.current ?: error("Account must exist")

    val validation = remember { mutableStateOf(ValidationContext()) }
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val compliance = remember { mutableStateOf<SignupProviderCompliance?>(null) }

    val accountKeyByCategory = remember {
        val filters = EnumSet.of(AccountKeyRequirement.REQUIRED, AccountKeyRequirement.COLLECTED)
        var result = account.configuration.allCategorized(filters).toMutableMap()

        account.details?.let { details ->
            if (details.isAnonymous) {
                result = result
                    .mapValues { entry -> entry.value.filter { !details.contains(it) } }
                    .filter { it.value.isNotEmpty() }
                    .toMutableMap()
            }
        }

        val requiredAccountKeys = account.accountService.configuration.requiredAccountKeys
        for (key in requiredAccountKeys) {
            if (result[key.category]?.contains(key) != true) {
                val list = result[key.category]?.toMutableList() ?: mutableListOf()
                list.add(key)
                result[key.category] = list
            }
        }

        result
    }

    ViewStateAlert(viewState)

    header()

    ReceiveValidation(validation) {
        CompositionLocalProvider(
            LocalAccountServiceConfiguration provides account.accountService.configuration,
            LocalAccountViewType provides AccountViewType.Signup
        ) {
            SignupSections(accountKeyByCategory)
        }

        SuspendButton(
            state = viewState,
            onClick = {
                if (!validation.value.validateHierarchy()) return@SuspendButton

                val details = AccountDetails()

                val anonymousDetails = account.details

                if (anonymousDetails != null && anonymousDetails.isAnonymous) {
                    val combined = details.copy()
                    combined.addContentsOf(anonymousDetails)
                    combined.validateAgainstSignupRequirements(account.configuration)
                } else {
                    details.validateAgainstSignupRequirements(account.configuration)
                }

                compliance.value = SignupProviderCompliance.compliant
                @Suppress("detekt:TooGenericExceptionCaught")
                try {
                    signup(details)
                } catch (throwable: Throwable) {
                    compliance.value = null
                    throw throwable
                }

                onDismissRequest()
            },
            enabled = validation.value.allInputValid
        ) {
            Text(
                StringResource("UP_SIGNUP").text(),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
        // .environment(\.defaultErrorDescription, .init("UP_SIGNUP_FAILED_DEFAULT_ERROR", bundle: .atURL(from: .module)))
    }
}

@ThemePreviews
@Composable
private fun SignupFormPreview() {
    SpeziTheme(isPreview = true) {
        SignupForm(
            signup = { details ->
                println("Signup Details: $details")
            },
            onDismissRequest = {}
        )
    }
}
