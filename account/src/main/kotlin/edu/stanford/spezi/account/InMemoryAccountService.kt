package edu.stanford.spezi.account

import edu.stanford.spezi.ui.validation.ValidationRule
import edu.stanford.spezi.ui.validation.intercepting
import edu.stanford.spezi.ui.validation.nonEmpty

/**
 * An in-memory implementation of the [AccountService] that can be used for testing or as a simple default implementation.
 */
class InMemoryAccountService : AccountService {
    override val configuration = accountServiceConfiguration(supportedAccountKeys = SupportedAccountKeys.Exactly(supportedAccountKeys)) {
        add(UserIdConfiguration(idType = UserIdType.Username))
        requiredKeys(AccountKeys.accountId::class, AccountKeys.userId::class)
        validationRule(keyType = AccountKeys.accountId::class, ValidationRule.nonEmpty.intercepting)
    }

    override suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun delete(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications): Result<Unit> {
        return Result.success(Unit)
    }

    private companion object {
        val supportedAccountKeys = accountKeyCollection(
            AccountKeys.accountId::class,
            AccountKeys.userId::class,
            AccountKeys.email::class,
            AccountKeys.password::class,
            AccountKeys.genderIdentity::class
        )
    }
}
