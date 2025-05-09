package edu.stanford.bdh.heartbeat.app.account

import com.google.firebase.auth.FirebaseAuth
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class AccountInfo(
    val id: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
)

interface AccountManager {
    fun observeAccountInfo(): Flow<AccountInfo?>

    suspend fun reloadAccountInfo(): Result<AccountInfo?>

    fun getAccountInfo(): AccountInfo?

    suspend fun getToken(): Result<String>

    suspend fun deleteCurrentUser(): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun sendForgotPasswordEmail(email: String): Result<Unit>

    suspend fun sendVerificationEmail(): Result<Unit>

    suspend fun signIn(email: String, password: String): Result<Unit>
}

@Singleton
class AccountManagerImpl @Inject internal constructor(
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
) : AccountManager {
    private val logger by speziLogger()
    private var userToken: String? = null

    init {
        observeUserTokenChanges()
    }

    private fun observeUserTokenChanges() {
        firebaseAuth.addIdTokenListener { auth: FirebaseAuth ->
            val currentUser = auth.currentUser
            if (currentUser == null) {
                userToken = null
            } else {
                auth.currentUser?.getIdToken(false)?.addOnSuccessListener { result ->
                    userToken = result.token
                }
            }
        }
    }

    override fun observeAccountInfo(): Flow<AccountInfo?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { _ ->
            coroutineScope.launch { send(getAccountInfo()) }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
            channel.close()
        }
    }

    override suspend fun reloadAccountInfo(): Result<AccountInfo?> = runCatching {
        firebaseAuth.currentUser?.reload()?.await()
        userToken = getToken(forceRefresh = true).getOrNull()
        getAccountInfo()
    }

    override suspend fun getToken(): Result<String> {
        return getToken(forceRefresh = false)
    }

    private suspend fun getToken(forceRefresh: Boolean): Result<String> {
        val currentReceivedToken = userToken.takeIf { forceRefresh.not() }
        if (currentReceivedToken != null) return Result.success(currentReceivedToken)
        return runCatching {
            val user =
                firebaseAuth.currentUser ?: error("Does not have a current user to get a token for")
            val token = user.getIdToken(forceRefresh).await().token ?: error("Id token refresh didn't include a token")
            userToken = token
            token
        }.onFailure {
            logger.e(it) { "Failed to retrieve token" }
        }
    }

    override suspend fun deleteCurrentUser(): Result<Unit> {
        return runCatching {
            firebaseAuth.currentUser?.delete()?.await() ?: error("User not available")
            Unit
        }.onFailure {
            logger.e { "Failed to delete user." }
        }
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        firebaseAuth.signOut()
    }.onFailure {
        logger.e { "Failed to sign out" }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (result?.user == null) error("Failed sign up with email and password")
        }.onFailure {
            logger.e(it) { "Error signing up with email and password" }
        }
    }

    override suspend fun sendForgotPasswordEmail(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await().let { }
        }.onFailure { e ->
            logger.e { "Error sending forgot password email: ${e.message}" }
        }
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        return runCatching {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            return@runCatching
        }.onFailure {
            logger.e { "Error sending verification email." }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) error("Failed to sign in, returned null user")
        }.onFailure { e ->
            logger.e { "Error signing in with email and password: ${e.message}" }
        }
    }

    override fun getAccountInfo(): AccountInfo? {
        return firebaseAuth.currentUser?.let { user ->
            AccountInfo(
                id = user.uid,
                email = user.email ?: "",
                name = user.displayName?.takeIf { it.isNotBlank() },
                isEmailVerified = user.isEmailVerified
            )
        }
    }
}
