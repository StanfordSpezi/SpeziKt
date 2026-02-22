package edu.stanford.spezi.account

/**
 * Bitmask-based options describing how an [AccountKey] behaves.
 *
 * Options can be combined using the `+` operator and checked using [contains].
 */
@JvmInline
value class AccountKeyOptions(private val raw: Int) {

    /**
     * Combines this option set with [other] and returns the result as a new [AccountKeyOptions] instance.
     */
    operator fun plus(other: AccountKeyOptions) = AccountKeyOptions(raw or other.raw)

    /**
     * Returns `true` if this option set contains [option].
     */
    fun contains(option: AccountKeyOptions) = raw and option.raw == option.raw

    companion object {
        /**
         * Option indication that the key should be displayed in account overview.
         */
        val Display = AccountKeyOptions(1 shl 0)

        /**
         * Option indicating that the key is mutable and can be changed / edited by the user.
         */
        val Mutable = AccountKeyOptions(1 shl 1)

        /**
         * A displayable and mutable key
         */
        val Default = Display + Mutable
    }
}
