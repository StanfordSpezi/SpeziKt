package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.ui.StringResource

/**
 * Configuration describing how a user-visible identifier (the "User ID") should be interpreted and presented.
 *
 * Many account-related UI components need to display a primary identifier to the user (e.g., an email address,
 * a username, or a custom label). [UserIdConfiguration] provides this information as an
 * [AccountServiceConfigurationKey] so it can be supplied by an [AccountService] (or defaulted if not provided).
 *
 * ### Defaults
 *
 * If not explicitly configured, the default value is [UserIdType.Email].
 */
data class UserIdConfiguration(
    /**
     * The type of user identifier that should be used (email, username, or custom).
     */
    val idType: UserIdType,
) : AccountServiceConfigurationKey<UserIdConfiguration>,
    DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, UserIdConfiguration> by Companion {

    companion object : DefaultProvidingKnowledgeSource<AccountServiceConfigurationAnchor, UserIdConfiguration> {
        /**
         * The default configuration used when no explicit [UserIdConfiguration] is supplied.
         */
        override val defaultValue: UserIdConfiguration =
            UserIdConfiguration(idType = UserIdType.Email)
    }
}

/**
 * Describes the kind of user identifier a service uses.
 *
 * This affects UI (labels, hints) and can be used by services or validation logic
 * to drive the appropriate input experience.
 */
sealed interface UserIdType {

    /**
     * The user identifier is an email address.
     */
    data object Email : UserIdType

    /**
     * The user identifier is a username (non-email identifier).
     */
    data object Username : UserIdType

    /**
     * A custom user identifier type with a user-facing label.
     *
     * @param label The localized label to display in UI (e.g., "Patient ID", "Employee ID").
     */
    data class Custom(val label: StringResource) : UserIdType
}

/**
 * Accessor for the configured [UserIdConfiguration] on [AccountServiceConfiguration].
 *
 * Implementations typically provide this configuration via the service configuration system.
 * If not set, consumers may obtain a default via [UserIdConfiguration.defaultValue] through
 * the knowledge source mechanism.
 */
var AccountServiceConfiguration.userIdConfiguration: UserIdConfiguration
    get() = storage[UserIdConfiguration::class]
    set(value) {
        storage[UserIdConfiguration::class] = value
    }
