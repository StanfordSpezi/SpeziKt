package edu.stanford.bdh.heartbeat.app.fake

import edu.stanford.bdh.heartbeat.app.account.AccountInfo
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAccountManager @Inject constructor() : AccountManager, FakeComponent {
    private val defaultAccount = AccountInfo(
        email = "fake-user@heartbeat-study.edu",
        name = "Fake User",
        isEmailVerified = FakeConfigs.EMAIL_VERIFIED,
    )

    private val accountState = MutableStateFlow(defaultAccount.takeIf { FakeConfigs.SKIP_LOGIN })

    override fun observeAccountInfo(): Flow<AccountInfo?> = accountState.asStateFlow()

    override suspend fun reloadAccountInfo(): Result<AccountInfo?> {
        delay()
        val newState = defaultAccount.copy(isEmailVerified = true)
        accountState.update { newState }
        return success(newState)
    }

    override suspend fun getToken(): Result<String> {
        return success("fake-user-token")
    }

    override suspend fun deleteCurrentUser(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun signOut(): Result<Unit> {
        accountState.update { null }
        return success(Unit)
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<Unit> {
        delay()
        accountState.update { defaultAccount.copy(email = email) }
        return success(Unit)
    }

    override suspend fun sendForgotPasswordEmail(email: String): Result<Unit> {
        return success(Unit)
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return signUpWithEmailAndPassword(email, password)
    }

    private fun <T> success(value: T) = Result.success(value)
}
