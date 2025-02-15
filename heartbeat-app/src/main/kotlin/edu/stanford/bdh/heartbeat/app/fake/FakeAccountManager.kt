package edu.stanford.bdh.heartbeat.app.fake

import edu.stanford.bdh.heartbeat.app.account.AccountInfo
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAccountManager @Inject constructor() : AccountManager {
    private val accountInfo = AccountInfo(
        email = "fake-user@heartbeat-study.edu",
        name = "Fake User",
        isEmailVerified = true,
    )

    override fun observeAccountInfo(): Flow<AccountInfo?> = flowOf(accountInfo)

    override fun getAccountInfo(): AccountInfo {
        return accountInfo
    }

    override suspend fun getToken(forceRefresh: Boolean): Result<String> {
        return success("fake-user-token")
    }

    override suspend fun deleteCurrentUser(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun signOut(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return success(Unit)
    }

    override suspend fun sendForgotPasswordEmail(email: String): Result<Unit> {
        return success(Unit)
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return success(Unit)
    }

    private fun <T> success(value: T) = Result.success(value)
}