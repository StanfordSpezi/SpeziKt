package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.minimalEmail
import edu.stanford.spezi.core.design.views.validation.minimalPassword
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.email
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import kotlin.reflect.KProperty0

data class FieldValidationRules(
    val key: AccountKey<String>,
    val rules: List<ValidationRule>,
) {
    companion object {
        private var keys = mutableMapOf<AccountKey<String>, FieldValidationRulesKey>()

        fun key(key: AccountKey<String>): OptionalComputedAccountServiceConfigurationKey<FieldValidationRules> {
            return keys[key] ?: run {
                val newValue = FieldValidationRulesKey(key)
                keys[key] = newValue
                return newValue
            }
        }
    }
}

private data class FieldValidationRulesKey(
    val key: AccountKey<String>,
) : OptionalComputedAccountServiceConfigurationKey<FieldValidationRules> {

    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
        get() = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

    override fun compute(repository: SharedRepository<AccountServiceConfigurationStorageAnchor>): FieldValidationRules? {
        return repository[FieldValidationRules.key(key)]?.let {
            return it // either the user configured a value themselves
        } ?: run {
            // or we return a default based on the Key type and the current configuration environment
            return if (
                key === AccountKeys.userId &&
                repository[UserIdConfiguration.key].idType == UserIdType.EmailAddress ||
                key === AccountKeys.email
            ) {
                FieldValidationRules(key = key, rules = listOf(ValidationRule.nonEmpty.intercepting, ValidationRule.minimalEmail))
            } else if (key === AccountKeys.password) {
                FieldValidationRules(key = key, rules = listOf(ValidationRule.nonEmpty.intercepting, ValidationRule.minimalPassword))
            } else {
                // we cannot statically determine here if the user may have configured the Key to be required
                null
            }
        }
    }
}

fun AccountServiceConfiguration.fieldValidationRules(key: AccountKey<String>): List<ValidationRule>? =
    storage[FieldValidationRules.key(key)]?.rules

fun AccountServiceConfiguration.fieldValidationRules(property: KProperty0<AccountKey<String>>): List<ValidationRule>? =
    storage[FieldValidationRules.key(property.get())]?.rules
