package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.minimalPassword
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.strongPassword
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfigurationPair
import edu.stanford.spezi.module.account.account.service.configuration.FieldValidationRules
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.fieldValidationRules
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.password

@Composable
internal fun PasswordValidationRuleFooter(
    configuration: AccountServiceConfiguration,
) {
    val rules = remember {
        configuration.fieldValidationRules(AccountKeys.password)
            ?.filter { it.id !== ValidationRule.nonEmpty.id } ?: emptyList()
    }

    Column {
        for (rule in rules) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Text(rule.message.text(), textAlign = TextAlign.Start)
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PasswordValidationRuleFooterPreview() {
    SpeziTheme(isPreview = true) {
        PasswordValidationRuleFooter(
            AccountServiceConfiguration(
                supportedKeys = SupportedAccountKeys.Arbitrary,
                configuration = listOf(
                    AccountServiceConfigurationPair(
                        FieldValidationRules.key(AccountKeys.password),
                        FieldValidationRules(AccountKeys.password, listOf(ValidationRule.minimalPassword, ValidationRule.strongPassword))
                    )
                )
            )
        )
    }
}
