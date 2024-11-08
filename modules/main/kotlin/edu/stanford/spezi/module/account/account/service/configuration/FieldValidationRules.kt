package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.email
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.foundation.knowledgesource.OptionalComputedKnowledgeSource
import edu.stanford.spezi.module.account.views.validation.ValidationRule
import edu.stanford.spezi.module.account.views.validation.minimalEmail
import edu.stanford.spezi.module.account.views.validation.minimalPassword
import edu.stanford.spezi.module.account.views.validation.nonEmpty
import java.util.UUID

data class FieldValidationRules(
    val key: AccountKey<String>,
    val rules: List<ValidationRule>,
) {
    companion object {
        private var keys = mutableMapOf<UUID, FieldValidationRulesKey>()

        fun key(key: AccountKey<String>): OptionalComputedAccountServiceConfigurationKey<
            FieldValidationRules,
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
            > {
            return keys[key.uuid] ?: run {
                val newValue = FieldValidationRulesKey(key)
                keys[key.uuid] = newValue
                return newValue
            }
        }
    }
}

private data class FieldValidationRulesKey(
    val key: AccountKey<String>,
    override val uuid: UUID = UUID(),
) : OptionalComputedAccountServiceConfigurationKey<FieldValidationRules, ComputedKnowledgeSourceStoragePolicy.AlwaysCompute>,
    OptionalComputedKnowledgeSource<
        AccountServiceConfigurationStorageAnchor,
        FieldValidationRules,
        ComputedKnowledgeSourceStoragePolicy.AlwaysCompute,
        AccountServiceConfigurationStorage
        > {

    override fun compute(repository: AccountServiceConfigurationStorage): FieldValidationRules? {
        return repository[FieldValidationRules.key(key)]?.let {
            return it // either the user configured a value themselves
        } ?: run {
            // or we return a default based on the Key type and the current configuration environment
            return if (
                key.isEqualTo(AccountKeys.userId) &&
                repository[UserIdConfiguration.key].idType == UserIdType.EmailAddress ||
                key.isEqualTo(AccountKeys.email)
            ) {
                FieldValidationRules(key = key, rules = listOf(ValidationRule.nonEmpty.intercepting, ValidationRule.minimalEmail))
            } else if (key.isEqualTo(AccountKeys.password)) {
                FieldValidationRules(key = key, rules = listOf(ValidationRule.nonEmpty.intercepting, ValidationRule.minimalPassword))
            } else {
                // we cannot statically determine here if the user may have configured the Key to be required
                null
            }
        }
    }
}
