package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngineConfiguration
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.compositionLocal.LocalPasswordFieldType
import edu.stanford.spezi.module.account.account.compositionLocal.PasswordFieldType
import edu.stanford.spezi.module.account.account.service.configuration.fieldValidationRules
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.accountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.keys.password
import java.util.EnumSet

@Composable
internal fun PasswordChangeSheet(
    model: AccountOverviewFormViewModel,
    details: AccountDetails,
    onClose: () -> Unit,
) {
    val validation = remember { mutableStateOf(ValidationContext()) }
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    val newPassword = remember { mutableStateOf("") }
    val newPasswordRepeat = remember { mutableStateOf("") }

    LaunchedEffect(newPasswordRepeat) {
        if (newPassword.value.isNotEmpty() && newPasswordRepeat.value.isNotEmpty()) {
            validation.value.validateHierarchy(switchFocus = false)
        }
        model.modifiedDetails.password = newPassword.value
    }

    ViewStateAlert(viewState)

    Column {
        val newPasswordRules = remember {
            details.accountServiceConfiguration
                .fieldValidationRules(AccountKeys.password) ?: emptyList()
        }
        Validate(newPassword.value, rules = newPasswordRules) {
            AccountKeys.password.EntryComposable(newPassword)
        }

        val validationEngineConfiguration = remember {
            EnumSet.of(ValidationEngine.ConfigurationOption.HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT)
        }
        val newPasswordRepeatRules = remember {
            listOf(
                ValidationRule(
                    rule = { it == newPassword.value },
                    message = StringResource("VALIDATION_RULE_PASSWORDS_NOT_MATCHED")
                )
            )
        }
        CompositionLocalProvider(
            LocalPasswordFieldType provides PasswordFieldType.REPEAT,
            LocalValidationEngineConfiguration provides validationEngineConfiguration,
        ) {
            Validate(newPasswordRepeat.value, rules = newPasswordRepeatRules) {
                AccountKeys.password.EntryComposable(newPasswordRepeat)
            }
        }

        PasswordValidationRuleFooter(details.accountServiceConfiguration)
    }
}

@ThemePreviews
@Composable
private fun PasswordChangeSheetPreview() {
    // PasswordChangeSheet(AccountOverviewFormViewModel()) { }
}
