package edu.stanford.spezi.account

import edu.stanford.spezi.account.internal.AccountModulesBuilder
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.SpeziDsl

/**
 * Configures the Spezi Account module.
 *
 * This is the primary entry point to register account functionality into a Spezi [edu.stanford.spezi.core.Configuration].
 * It wires an [AccountService], an optional [AccountStorageProvider], an optional initial user state,
 * and a user-defined [AccountValueConfiguration] constructed via [AccountValueConfigurationBuilder].
 *
 * ### Account keys configuration
 *
 * Using the [configuration] DSL, you define which [AccountKey]s are:
 * - **required** for a valid account,
 * - **collected** during signup,
 * - **supported** (display-only), or
 * - **manual** (managed outside of the standard collection flow).
 *
 * The resulting configuration is stored on `Account.configuration`.
 *
 * ### Initial state
 *
 * If [initialDetails] is provided, the account starts in an associated/signed-in state.
 * Otherwise, the account starts without an associated user.
 *
 * @param service The [AccountService] responsible for managing account association and state changes.
 * @param storageProvider Optional provider to persist and retrieve additional account values that are not managed
 *        by the [service].
 * @param initialDetails Optional initial [AccountDetails]. If present, the account is considered associated.
 * @param configuration DSL to declare configured account keys and their requirements.
 */
@SpeziDsl
fun ConfigurationBuilder.accountConfiguration(
    service: AccountService,
    storageProvider: AccountStorageProvider? = null,
    initialDetails: AccountDetails? = null,
    configuration: AccountValueConfigurationBuilder.() -> Unit,
) {
    val modulesBuilder = AccountModulesBuilder(
        service = service,
        storageProvider = storageProvider,
        valueConfigurationBuilder = configuration,
        initialDetails = initialDetails
    )
    modulesBuilder.register(configurationBuilder = this)
}

/**
 * DSL builder to declare the account keys that all user accounts need to support.
 *
 * This builder produces an [AccountValueConfiguration] by collecting [AccountKeyConfiguration] entries
 * keyed by [AccountKey.identifier].
 *
 * ### Requirements
 *
 * The following constraints are enforced:
 * - [requires] / [collects] keys must be **displayable** and **mutable**.
 * - [supports] keys must be **displayable**.
 * - A key identifier must not be blank.
 * - A key identifier may only be configured once.
 *
 * Typical usage:
 * ```kotlin
 * accountConfiguration(
 *   service = ...,
 *   keys = {
 *     requires(AccountKeys.accountId)
 *     collects(AccountKeys.email)
 *     supports(AccountKeys.genderIdentity)
 *     manual(AccountKeys.userId)
 *   }
 * )
 * ```
 */
@SpeziDsl
class AccountValueConfigurationBuilder {
    private val configurations = mutableMapOf<String, AccountKeyConfiguration<*>>()

    /**
     * Marks the given [key] as required.
     *
     * Required keys are expected to be present for a valid, fully configured account.
     * A key can only be required if it is both displayable and mutable.
     */
    fun requires(key: AnyAccountKey) {
        validateFor(key = key, requirement = AccountKeyRequirement.REQUIRED)
        add(key = key, requirement = AccountKeyRequirement.REQUIRED)
    }

    /**
     * Marks the given [key] as collected during signup/onboarding.
     *
     * Collected keys are not strictly required for a valid account, but are part of the standard
     * collection flow. A key can only be collected if it is both displayable and mutable.
     */
    fun collects(key: AnyAccountKey) {
        validateFor(key = key, requirement = AccountKeyRequirement.COLLECTED)
        add(key = key, requirement = AccountKeyRequirement.COLLECTED)
    }

    /**
     * Marks the given [key] as supported (displayable) by the account configuration.
     *
     * Supported keys are expected to be displayable, but may not be mutable/collectable
     * through the standard flow.
     */
    fun supports(key: AnyAccountKey) {
        validateFor(key = key, requirement = AccountKeyRequirement.SUPPORTED)
        add(key = key, requirement = AccountKeyRequirement.SUPPORTED)
    }

    /**
     * Marks the given [key] as manually managed.
     *
     * Manual keys are intentionally excluded from the standard validation constraints and
     * are expected to be supplied/managed by the application or a custom flow.
     */
    fun manual(key: AnyAccountKey) {
        add(key = key, requirement = AccountKeyRequirement.MANUAL)
    }

    /**
     * Builds the immutable [AccountValueConfiguration] from the configured keys.
     */
    internal fun buildConfiguration(): AccountValueConfiguration = AccountValueConfiguration(configurations = configurations.values.toSet())

    private fun add(key: AnyAccountKey, requirement: AccountKeyRequirement) {
        val id = key.identifier
        require(id.isNotBlank()) { "AccountKey.identifier must not be blank." }
        require(!configurations.containsKey(id)) { "AccountKey '$id' configured more than once." }
        configurations[id] = AccountKeyConfiguration(key = key, requirement = requirement)
    }

    private fun validateFor(key: AccountKey<*>, requirement: AccountKeyRequirement) {
        when (requirement) {
            AccountKeyRequirement.REQUIRED,
            AccountKeyRequirement.COLLECTED,
            -> {
                val options = key.options
                require(options.contains(AccountKeyOptions.Display) && options.contains(AccountKeyOptions.Mutable)) {
                    "AccountKey '${key.identifier}' can only be $requirement if it is displayable and mutable."
                }
            }

            AccountKeyRequirement.SUPPORTED -> require(key.options.contains(AccountKeyOptions.Display)) {
                "AccountKey '${key.identifier}' can only be SUPPORTED if it is displayable."
            }

            AccountKeyRequirement.MANUAL -> Unit
        }
    }
}
