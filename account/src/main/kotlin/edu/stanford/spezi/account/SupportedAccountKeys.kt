package edu.stanford.spezi.account

/**
 * The collection of [AccountKey]s that an [AccountService] is capable of storing itself.
 *
 * An [AccountService] must provide this configuration option to communicate which set of account keys
 * it can persist.
 *
 * On startup, SpeziAccount verifies that the user-configured account values are either:
 * - supported by the configured [AccountService], or
 * - backed by an [AccountStorageProvider] that can handle storage of all unsupported account values.
 *
 * ### Example
 *
 * Provide a fixed set of supported keys:
 *
 * ```kotlin
 * val supportedKeys = accountKeyCollection(
 *  AccountKeys.accountId::class,
 *  AccountKeys.userId::class,
 *  AccountKeys.email::class,
 *  AccountKeys.password::class,
 *  AccountKeys.genderIdentity::class
 * )
 *
 * val configuration = AccountServiceConfiguration(
 *   supportedAccountKeys = SupportedAccountKeys.Exactly(supportedKeys)
 * )
 * ```
 */
sealed interface SupportedAccountKeys : AccountServiceConfigurationKey<SupportedAccountKeys> {

    /**
     * The [AccountService] is capable of storing arbitrary account keys.
     */
    data object Arbitrary : SupportedAccountKeys

    /**
     * The [AccountService] is capable of storing only a fixed set of account keys.
     */
    data class Exactly(val keys: AccountKeyCollection) : SupportedAccountKeys
}
