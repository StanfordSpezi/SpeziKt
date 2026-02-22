package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.ui.validation.ValidationRule

/**
 * Configuration for an account service, defining supported account keys and additional configuration options.
 */
class AccountServiceConfiguration(
    val supportedAccountKeys: SupportedAccountKeys,
    configuration: Set<AccountServiceConfigurationKey<*>>,
) {
    /**
     * Storage for the account service configuration, containing the supported account keys and any additional configuration entries.
     */
    val storage: AccountServiceConfigurationStorage = AccountServiceConfigurationStorage().apply {
        this[SupportedAccountKeys::class] = supportedAccountKeys
        configuration.forEach { it.storeIn(this) }
    }

    /**
     * Convenience constructor for creating an [AccountServiceConfiguration] with just supported account keys
     * and no additional configuration.
     */
    constructor(supportedAccountKeys: SupportedAccountKeys) : this(
        supportedAccountKeys = supportedAccountKeys,
        configuration = emptySet(),
    )

    /**
     * Convenience constructor for creating an [AccountServiceConfiguration] with supported account keys and
     * a variable number of additional configuration entries.
     */
    constructor(
        supportedAccountKeys: SupportedAccountKeys,
        vararg configuration: AccountServiceConfigurationKey<*>,
    ) : this(
        supportedAccountKeys = supportedAccountKeys,
        configuration = configuration.toSet(),
    )
}

/**
 * Builder for constructing an [AccountServiceConfiguration] in a DSL style.
 *
 * @param supportedAccountKeys the supported account keys to be included in the configuration
 */
class AccountServiceConfigurationBuilder(
    private var supportedAccountKeys: SupportedAccountKeys,
) {
    private val validationRules = mutableSetOf<FieldValidationRules>()
    private val configuration: MutableSet<AccountServiceConfigurationKey<*>> = mutableSetOf()

    /**
     * Adds a configuration entry to the account service configuration being built.
     */
    fun <T : Any> add(key: AccountServiceConfigurationKey<T>) {
        configuration.add(key)
    }

    /**
     * Convenience method for adding a required account key configuration entry to the account service configuration being built.
     */
    fun requiredKeys(vararg key: AccountKeyType<*>) {
        add(RequiredAccountKeys(keys = key.toSet()))
    }

    /**
     * Convenience method for adding a supported account key configuration entry to the account service configuration being built.
     */
    fun validationRule(keyType: AccountKeyType<String>, rules: Set<ValidationRule>) {
        validationRules.add(FieldValidationRules(keyType = keyType, validationRules = rules))
    }

    /**
     * Convenience method for adding a supported account key configuration entry to the account service configuration being built,
     */
    fun validationRule(keyType: AccountKeyType<String>, vararg rules: ValidationRule) {
        validationRules.add(FieldValidationRules(keyType = keyType, validationRules = rules.toSet()))
    }

    internal fun build(): AccountServiceConfiguration {
        if (validationRules.isNotEmpty()) add(FieldValidationRulesCollection(fieldRules = validationRules.toSet()))
        return AccountServiceConfiguration(
            supportedAccountKeys = supportedAccountKeys,
            configuration = configuration,
        )
    }
}

/**
 * Convenience function for creating an [AccountServiceConfiguration] using a DSL style.
 */
fun accountServiceConfiguration(
    supportedAccountKeys: SupportedAccountKeys,
    block: AccountServiceConfigurationBuilder.() -> Unit,
): AccountServiceConfiguration {
    val builder = AccountServiceConfigurationBuilder(supportedAccountKeys)
    builder.block()
    return builder.build()
}

private object AccountServiceConfigurationDetailsKey : DefaultProvidingKnowledgeSource<AccountAnchor, AccountServiceConfiguration> {
    override val defaultValue: AccountServiceConfiguration
        get() = AccountServiceConfiguration(supportedAccountKeys = SupportedAccountKeys.Exactly(keys = emptySet()))
}

/**
 * Extension property for accessing the [AccountServiceConfiguration] from an [AccountStorage].
 */
var AccountStorage.accountServiceConfiguration: AccountServiceConfiguration
    get() = this[AccountServiceConfigurationDetailsKey::class]
    set(value) { this[AccountServiceConfigurationDetailsKey::class] = value }

/**
 * Extension property for accessing the [AccountServiceConfiguration] from an [AccountDetails].
 */
var AccountDetails.accountServiceConfiguration: AccountServiceConfiguration
    get() = this[AccountServiceConfigurationDetailsKey::class]
    set(value) { this[AccountServiceConfigurationDetailsKey::class] = value }

/**
 * Extension property for accessing the [UserIdType] from the [AccountServiceConfiguration] stored in an [AccountDetails].
 */
val AccountDetails.userIdType: UserIdType
    get() = accountServiceConfiguration.storage[UserIdConfiguration::class].idType
