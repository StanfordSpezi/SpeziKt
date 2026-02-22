package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.DefaultProvidingKnowledgeSource

/**
 * Bitmask-based flags stored inside [AccountDetails] to represent common account state.
 *
 * This is a small value type wrapping an `Int` bitmask and provides convenience operators for
 * adding/removing/checking flags.
 *
 * Use [contains] to check whether a given option is set.
 */
@JvmInline
value class AccountDetailFlags(private val raw: Int) {

    /**
     * Adds all flags from [other] to this flag set and returns the result as a new [AccountDetailFlags] instance.
     */
    operator fun plus(other: AccountDetailFlags) = AccountDetailFlags(raw or other.raw)

    /**
     * Removes all flags from [other] from this flag set and returns the result as a new [AccountDetailFlags] instance.
     */
    operator fun minus(other: AccountDetailFlags): AccountDetailFlags = AccountDetailFlags(raw and other.raw.inv())

    /**
     * Checks if all flags from [option] are present in this flag set.
     *
     * @return `true` if all flags in [option] are set in this flag set, `false` otherwise
     */
    fun contains(option: AccountDetailFlags) = raw and option.raw == option.raw

    companion object {
        /**
         * Flag indicating that the account belongs to a new user who has not completed onboarding.
         */
        val isNewUser = AccountDetailFlags(1 shl 0)

        /**
         * Flag indicating that the account belongs to an anonymous user without a registered identity.
         */
        val isAnonymousUser = AccountDetailFlags(1 shl 1)

        /**
         * Flag indicating that the account has been verified, e.g. via email confirmation.
         */
        val isVerified = AccountDetailFlags(1 shl 2)

        /**
         * Flag indicating that the account details are incomplete and require additional information.
         */
        val isIncomplete = AccountDetailFlags(1 shl 3)
    }
}

/**
 * Whether the account is considered a new user.
 *
 * Backed by [AccountDetailFlags.isNewUser] stored in [AccountDetails].
 * Defaults to `false`.
 */
var AccountDetails.isNewUser: Boolean
    get() = flags.contains(AccountDetailFlags.isNewUser)
    set(value) = updateFlags(update = AccountDetailFlags.isNewUser, include = value)

/**
 * Whether the account is considered an anonymous user.
 *
 * Backed by [AccountDetailFlags.isAnonymousUser] stored in [AccountDetails].
 * Defaults to `false`.
 */
var AccountDetails.isAnonymousUser: Boolean
    get() = flags.contains(AccountDetailFlags.isAnonymousUser)
    set(value) = updateFlags(update = AccountDetailFlags.isAnonymousUser, include = value)

/**
 * Whether the account is considered verified.
 *
 * Backed by [AccountDetailFlags.isVerified] stored in [AccountDetails].
 * Defaults to `false`.
 */
var AccountDetails.isVerified: Boolean
    get() = flags.contains(AccountDetailFlags.isVerified)
    set(value) = updateFlags(update = AccountDetailFlags.isVerified, include = value)

/**
 * Whether the account is considered incomplete.
 *
 * Backed by [AccountDetailFlags.isIncomplete] stored in [AccountDetails].
 * Defaults to `false`.
 */
var AccountDetails.isIncomplete: Boolean
    get() = flags.contains(AccountDetailFlags.isIncomplete)
    set(value) = updateFlags(AccountDetailFlags.isIncomplete, value)

private object AccountDetailsFlagsKey : DefaultProvidingKnowledgeSource<AccountAnchor, AccountDetailFlags> {
    override val defaultValue: AccountDetailFlags = AccountDetailFlags(0)
}

private val AccountDetails.flags: AccountDetailFlags
    get() = this[AccountDetailsFlagsKey::class]

private fun AccountDetails.updateFlags(update: AccountDetailFlags, include: Boolean) {
    set(
        key = AccountDetailsFlagsKey::class,
        value = if (include) flags + update else flags - update
    )
}
