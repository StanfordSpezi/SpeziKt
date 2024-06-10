package edu.stanford.spezi.module.account.cred.manager

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import javax.inject.Inject

class CredentialLoginManagerAuth @Inject constructor(
    private val credentialManager: CredentialManager,
    private val firebaseAuthManager: FirebaseAuthManager,
    @ApplicationContext private val context: Context,
) {
    private val logger by speziLogger()

    suspend fun handleGoogleSignIn(): Result<Boolean> {
        val result = getCredential(true)
        return if (result != null) {
            firebaseAuthManager.signInWithGoogle(result.idToken)
        } else {
            Result.success(false)
        }
    }

    suspend fun handlePasswordSignIn(
        username: String,
        password: String,
    ): Boolean {
        val createPasswordRequest = CreatePasswordRequest(id = username, password = password)
        val createCredential = credentialManager.createCredential(context, createPasswordRequest)
        if (createCredential.type == PasswordCredential.TYPE_PASSWORD_CREDENTIAL) {
            return firebaseAuthManager.signInWithEmailAndPassword(username, password)
        }
        return false
    }

    private suspend fun getCredential(filterByAuthorizedAccounts: Boolean): GoogleIdTokenCredential? {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setAutoSelectEnabled(true)
                // TODO: Uncomment once secrets xml has been added in CI secrets
                // .setServerClientId(context.getString(R.string.serverClientId))
                .build()

            val passwordOption = GetPasswordOption(
                isAutoSelectAllowed = true,
            )

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                // TODO .addCredentialOption(passwordOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )
            when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        return googleIdTokenCredential
                    }
                    if (credential.type == PasswordCredential.TYPE_PASSWORD_CREDENTIAL) {
                        TODO()
                    }
                }
            }
            return null
        } catch (e: GetCredentialException) {
            logger.e { "Error getting credential: ${e.message}" }
            null
        }
    }

    suspend fun sendForgotPasswordEmail(email: String): Result<Void> {
        return firebaseAuthManager.sendForgotPasswordEmail(email)
    }
}
