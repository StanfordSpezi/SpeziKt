package edu.stanford.spezi.module.account.account.views.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.EntryWithEmptyValue
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.views.overview.PasswordValidationRuleFooter

@Composable
internal fun SignupSections(
    sections: Map<AccountKeyCategory, List<AccountKey<*>>>,
) {
    val account = LocalAccount.current

    Column {
        for (entry in sections.entries) {
            Column {
                entry.key.categoryTitle?.let {
                    Text(it.text()) // TODO: Section header
                }

                for (key in entry.value) {
                    Column {
                        key.EntryWithEmptyValue()
                    }
                }

                val isCredentialsCategory = entry.key == AccountKeyCategory.credentials
                val password = (account?.configuration?.configuration ?: emptyMap())[AccountKeys.password]
                if (isCredentialsCategory && password != null) {
                    account?.accountService?.configuration?.let {
                        PasswordValidationRuleFooter(it)
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
internal fun SignupSectionsPreview() {
    SignupSections(
        mapOf(
            AccountKeyCategory.credentials to listOf(AccountKeys.userId, AccountKeys.password),
            AccountKeyCategory.name to listOf(AccountKeys.name)
        )
    )
}
