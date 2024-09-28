package edu.stanford.spezi.module.account.manager

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.R
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class AuthenticationManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    @ApplicationContext private val context: Context,
) {
    private val logger by speziLogger()

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

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) error("Failed to sign in, returned null user")
        }.onFailure { e ->
            logger.e { "Error signing in with email and password: ${e.message}" }
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        return runCatching {
            val googleIdTokenCredential =
                getCredential(filterByAuthorizedAccounts = true)
                    ?: getCredential(filterByAuthorizedAccounts = false)
                    ?: error("Failed to get credentials.")

            val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            logger.i { "Result: $result" }
            if (result.user == null) error("Failed to sign in, returned null user")
        }.onFailure {
            logger.e { "Error signing in with google: ${it.message}" }
        }
    }

    suspend fun getCredential(filterByAuthorizedAccounts: Boolean): GoogleIdTokenCredential? {
        return runCatching {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setAutoSelectEnabled(true)
                .setServerClientId(context.getString(R.string.serverClientId))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credential = credentialManager.getCredential(
                request = request,
                context = context
            ).credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                GoogleIdTokenCredential.createFrom(credential.data)
            } else {
                null
            }
        }.onFailure { e ->
            logger.e { "Error getting credential: ${e.message}" }
        }.getOrNull()
    }
}
