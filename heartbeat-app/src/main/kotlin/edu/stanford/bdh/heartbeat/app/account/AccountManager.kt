package edu.stanford.bdh.heartbeat.app.account

import com.google.firebase.auth.FirebaseAuth
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AccountInfo(
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
)

class AccountManager @Inject internal constructor(
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
) {
    private val logger by speziLogger()

    fun observeAccountInfo(): Flow<AccountInfo?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { _ ->
            coroutineScope.launch { send(getAccountInfo()) }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
            channel.close()
        }
    }

    fun getAccountInfo(): AccountInfo? {
        return firebaseAuth.currentUser?.let { user ->
            AccountInfo(
                email = user.email ?: "",
                name = user.displayName?.takeIf { it.isNotBlank() },
                isEmailVerified = user.isEmailVerified
            )
        }
    }

    suspend fun getToken(forceRefresh: Boolean = false): Result<String> {
        return runCatching {
            val user = firebaseAuth.currentUser ?: error("Does not have a current user to get a token for")
            val idToken = user.getIdToken(forceRefresh).await()
            return@runCatching idToken.token ?: error("Id token refresh didn't include a token")
        }.onFailure {
            logger.e { "Failed to force refresh token" }
        }
    }

    fun signOut() {
        runCatching {
            firebaseAuth.signOut()
        }.onFailure {
            logger.e { "Failed to sign out" }
        }
    }

    suspend fun signUpWithEmailAndPassword(
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

    suspend fun sendForgotPasswordEmail(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await().let { }
        }.onFailure { e ->
            logger.e { "Error sending forgot password email: ${e.message}" }
        }
    }

    suspend fun sendVerificationEmail(): Result<Unit> {
        return runCatching {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            return@runCatching
        }.onFailure {
            logger.e { "Error sending verification email." }
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) error("Failed to sign in, returned null user")
        }.onFailure { e ->
            logger.e { "Error signing in with email and password: ${e.message}" }
        }
    }
}
