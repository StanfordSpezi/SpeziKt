package edu.stanford.spezi.account

/**
 * An [AccountStorageProvider] implementation that does not persist any data and always returns null for load operations.
 *
 * This can be used for testing or in scenarios where account data persistence is not required.
 */
class InMemoryAccountStorageProvider : AccountStorageProvider {
    override suspend fun load(
        accountId: String,
        keys: Set<AnyAccountKey>,
    ): Result<AccountDetails?> {
        return Result.success(null)
    }

    override suspend fun store(
        accountId: String,
        modifications: AccountModifications,
    ): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun disassociate(accountId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun delete(accountId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
