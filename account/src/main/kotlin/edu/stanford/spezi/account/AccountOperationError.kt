package edu.stanford.spezi.account

/**
 * Errors that can occur during account operations.
 */
sealed class AccountOperationError : Throwable() {

    /**
     * Indicates that the account ID was changed during modification operations, which is not allowed.
     */
    class AccountIdChanged : AccountOperationError()

    /**
     * Indicates that the account value does not contain required keys as per configuration.
     */
    class MissingAccountValue(val keys: List<String>) : AccountOperationError()

    /**
     *  Tried to modify account details that are not supported to be modified from the client side.
     */
    class MutatingNonMutableAccountKeys(val keys: List<String>) : AccountOperationError()
}
