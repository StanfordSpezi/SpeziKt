package edu.stanford.spezi.account

import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.validation.ValidationRule
import edu.stanford.spezi.ui.validation.intercepting
import edu.stanford.spezi.ui.validation.minimalEmail
import edu.stanford.spezi.ui.validation.minimalPassword
import edu.stanford.spezi.ui.validation.nonEmpty

/**
 * An in-memory implementation of the [AccountService] that can be used for testing or as a simple default implementation.
 *
 * This service stores credentials in memory and is not persisted across app restarts.
 * It is intended to be used for testing and demo purposes, such as in sample apps or test screens.
 *
 * ## Example:
 *
 * ```kotlin
 * class MyApplication : Application(), SpeziApplication {
 *
 *     override val configuration = Configuration {
 *         accountConfiguration(
 *             service = InMemoryAccountService(),
 *         )
 *     }
 * }
 * ```
 */
class InMemoryAccountService(
    override val configuration: AccountServiceConfiguration = defaultConfiguration,
) : AccountService {

    private val account by dependency<Account>()

    override suspend fun signUp(signupDetails: AccountDetails): Result<Unit> {
        account.supplyUserDetails(signupDetails)
        return Result.success(Unit)
    }

    override suspend fun login(credential: UserIdPasswordCredential): Result<Unit> {
        val details = AccountDetails().apply {
            this[AccountKeys.accountId::class] = credential.userId
            this[AccountKeys.userId::class] = credential.userId
        }
        return signUp(details)
    }

    override suspend fun logout(): Result<Unit> {
        account.removeUserDetails()
        return Result.success(Unit)
    }

    override suspend fun delete(): Result<Unit> {
        account.removeUserDetails()
        return Result.success(Unit)
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications): Result<Unit> {
        val current = account.details.value ?: return Result.failure(IllegalStateException("No user is currently signed in"))
        current.addContents(modifications.modifiedDetails)
        current.removeAll(modifications.removedAccountKeys)
        account.supplyUserDetails(current)
        return Result.success(Unit)
    }

    override suspend fun onPasswordForgotten(userId: String): Result<Unit> {
        // No-op for in-memory: there is no external reset mechanism
        return Result.success(Unit)
    }

    override suspend fun signIn(provider: AuthProvider): Result<Unit> {
        return if (provider is InMemoryProvider) {
            val accountDetails = AccountDetails().apply {
                val email = "spezi@stanford.edu"
                this[AccountKeys.accountId::class] = email
                this[AccountKeys.userId::class] = email
                this[AccountKeys.name::class] = PersonName(givenName = "John", familyName = "Doe")
                this[AccountKeys.email::class] = email
            }
            account.supplyUserDetails(accountDetails)
            Result.success(Unit)
        } else {
            Result.failure(UnsupportedOperationException("Provider-based sign-in is not supported by InMemoryAccountService"))
        }
    }

    private companion object {
        val supportedAccountKeys = accountKeyCollection(
            AccountKeys.accountId::class,
            AccountKeys.userId::class,
            AccountKeys.email::class,
            AccountKeys.name::class,
            AccountKeys.password::class,
            AccountKeys.dateOfBirth::class,
            AccountKeys.genderIdentity::class,
        )

        val defaultConfiguration = accountServiceConfiguration(
            supportedAccountKeys = SupportedAccountKeys.Exactly(supportedAccountKeys),
        ) {
            add(UserIdConfiguration(idType = UserIdType.Username))
            requiredKeys(AccountKeys.accountId::class, AccountKeys.userId::class, AccountKeys.name::class)
            validationRule(keyType = AccountKeys.userId::class, ValidationRule.nonEmpty.intercepting)
            validationRule(keyType = AccountKeys.email::class, ValidationRule.minimalEmail.intercepting)
            validationRule(keyType = AccountKeys.password::class, ValidationRule.minimalPassword)
            authProvider(InMemoryProvider())
        }
    }

    private class InMemoryProvider : AuthProvider {
        override val actionName = StringResource(Strings.account_in_memory_sign_up_action)
        override val icon: ImageResource? = null
    }
}
