package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.foundation.OptionalComputedKnowledgeSource
import edu.stanford.spezi.foundation.ValueRepository
import edu.stanford.spezi.ui.validation.ValidationRule
import edu.stanford.spezi.ui.validation.intercepting
import edu.stanford.spezi.ui.validation.minimalEmail
import edu.stanford.spezi.ui.validation.minimalPassword
import edu.stanford.spezi.ui.validation.nonEmpty

/**
 * Defines the validation rules for a specific account field, identified by its [keyType] for string based account keys.
 *
 * @property keyType The type of the account key for which the validation rules apply.
 * @property validationRules A set of [ValidationRule]s that should be applied to the field.
 */
data class FieldValidationRules(
    val keyType: AccountKeyType<String>,
    val validationRules: Set<ValidationRule>,
)

/**
 * A collection of [FieldValidationRules] for different account fields,
 * stored as a configuration entry in the account service configuration.
 *
 * This class implements [OptionalComputedKnowledgeSource] to allow for dynamic computation of the validation rules
 * based on other configuration entries, such as the user ID type.
 *
 * If no rules have been specified, a default collection is returned.
 * For example, if the user ID type is email, specific validation rules for email format will be included for the user ID field. Email
 * and password fields will also have their own default validation rules applied (e.g., non-empty, minimal length, email format etc).
 *
 * The computed validation rules are stored in the account service configuration
 * and can be accessed by other components of the account service when needed.
 */
data class FieldValidationRulesCollection(
    val fieldRules: Set<FieldValidationRules>,
) : AccountServiceConfigurationKey<FieldValidationRulesCollection>,
    OptionalComputedKnowledgeSource<AccountServiceConfigurationAnchor, FieldValidationRulesCollection> {

    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

    override fun compute(repository: ValueRepository<AccountServiceConfigurationAnchor>): FieldValidationRulesCollection? {
        val rules = repository.getOrNull(FieldValidationRulesCollection::class)
        if (rules != null) return rules

        val fieldRules = buildSet {
            if (repository[UserIdConfiguration::class].idType == UserIdType.Email) {
                add(
                    FieldValidationRules(
                        keyType = UserIdKey::class,
                        validationRules = setOf(
                            ValidationRule.nonEmpty.intercepting,
                            ValidationRule.minimalEmail.intercepting,
                        )
                    )
                )
            }
            add(
                FieldValidationRules(
                    keyType = EmailKey::class,
                    validationRules = setOf(
                        ValidationRule.nonEmpty.intercepting,
                        ValidationRule.minimalEmail,
                    )
                )
            )
            add(
                FieldValidationRules(
                    keyType = PasswordKey::class,
                    validationRules = setOf(
                        ValidationRule.nonEmpty.intercepting,
                        ValidationRule.minimalPassword,
                    )
                )
            )
        }
        return FieldValidationRulesCollection(fieldRules = fieldRules)
    }
}

/**
 * Convenience extension function for retrieving the validation rules for a specific account key type
 * from the account service configuration.
 */
fun AccountServiceConfiguration.fieldValidationRules(key: AccountKeyType<String>): Set<ValidationRule>? {
    val collection = storage[FieldValidationRulesCollection::class]
    val matchingRules = collection?.fieldRules?.find { it.keyType == key }
    return matchingRules?.validationRules?.toSet()
}
