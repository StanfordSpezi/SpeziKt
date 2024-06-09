@file:Suppress("LongParameterList")

package edu.stanford.spezi.module.account.cred.manager

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.R
import java.time.LocalDate
import javax.inject.Inject

class CredentialRegisterManagerAuth @Inject internal constructor(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val credentialManager: CredentialManager,
) {

    private val logger by speziLogger()

    suspend fun googleSignUp(
        idToken: String,
        firstName: String,
        lastName: String,
        email: String,
        selectedGender: String,
        dateOfBirth: LocalDate,
    ): Result<Boolean> {
        return runCatching {
            val linkResult = firebaseAuthManager.linkUserToGoogleAccount(idToken)
            val saveResult = if (linkResult) {
                firebaseAuthManager.saveUserData(
                    firstName = firstName,
                    lastName = lastName,
                    selectedGender = selectedGender,
                    email = email,
                    dateOfBirth = dateOfBirth,
                )
            } else {
                false
            }
            linkResult && saveResult
        }
    }

    suspend fun passwordAndEmailSignUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        selectedGender: String,
        dateOfBirth: LocalDate,
    ): Result<Boolean> {
        return firebaseAuthManager.signUpWithEmailAndPassword(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )
    }

    suspend fun getGoogleSignUpCredential(
        context: Context,
    ): GoogleIdTokenCredential? {
        var result = getCredential(context = context, filterByAuthorizedAccounts = true)
        if (result == null) {
            logger.i { "No authorized accounts found" }
            result = getCredential(context = context, filterByAuthorizedAccounts = false)
        }
        return result
    }

    private suspend fun getCredential(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
    ): GoogleIdTokenCredential? {
        return runCatching {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setAutoSelectEnabled(true)
                .setServerClientId(context.getString(R.string.serverClientId))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )
            when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        GoogleIdTokenCredential.createFrom(credential.data)
                    } else {
                        null
                    }
                }

                else -> null
            }
        }.onFailure { e ->
            logger.e { "Error getting credential: ${e.message}" }
        }.getOrNull()
    }
}
