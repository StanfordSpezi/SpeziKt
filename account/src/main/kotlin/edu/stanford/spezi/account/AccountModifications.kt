package edu.stanford.spezi.account

/**
 * Represents modifications to an account's details, including both changes to existing details and removals of details.
 */
class AccountModifications private constructor(
    private val modifiedDetails: AccountDetails,
    private val removedAccountDetails: AccountDetails,
) {

    /**
     * The set of account key types that have been removed.
     */
    val removedAccountKeys: Set<AnyAccountKeyType>
        get() = removedAccountDetails.accountKeyTypes

    /**
     * Indicates whether there are no modifications or removals in this instance.
     */
    val isEmpty: Boolean
        get() = modifiedDetails.isEmpty && removedAccountDetails.isEmpty

    /**
     * Removes the specified account key types from both modifications and removals.
     */
    fun removeModifications(keys: Set<AnyAccountKeyType>) {
        modifiedDetails.removeAll(keys)
        removedAccountDetails.removeAll(keys)
    }

    companion object {

        /**
         * Creates an [AccountModifications] instance with the specified modified details and removed account details.
         */
        operator fun invoke(
            modifiedDetails: AccountDetails = AccountDetails(),
            removedAccountDetails: AccountDetails = AccountDetails(),
        ): Result<AccountModifications> {
            if (modifiedDetails.contains(AccountKeys.accountId) || removedAccountDetails.contains(AccountKeys.accountId)) {
                return Result.failure(AccountOperationError.AccountIdChanged())
            }

            return Result.success(AccountModifications(modifiedDetails = modifiedDetails, removedAccountDetails = removedAccountDetails))
        }
    }
}
