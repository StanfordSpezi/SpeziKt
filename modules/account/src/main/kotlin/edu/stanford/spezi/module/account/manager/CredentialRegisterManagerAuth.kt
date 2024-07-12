@file:Suppress("LongParameterList")

package edu.stanford.spezi.module.account.manager

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import java.time.LocalDate
import javax.inject.Inject

internal class CredentialRegisterManagerAuth @Inject internal constructor(
    private val authenticationManager: AuthenticationManager,
    private val credentialManager: CredentialManager,
    @ApplicationContext private val context: Context,
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
            val linkResult = authenticationManager.linkUserToGoogleAccount(idToken)
            val saveResult = if (linkResult) {
                authenticationManager.saveUserData(
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
        return authenticationManager.signUpWithEmailAndPassword(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )
    }

    suspend fun getGoogleSignUpCredential(): GoogleIdTokenCredential? {
        var result = getCredential(filterByAuthorizedAccounts = true)
        if (result == null) {
            logger.i { "No authorized accounts found" }
            result = getCredential(filterByAuthorizedAccounts = false)
        }
        return result
    }

    private suspend fun getCredential(filterByAuthorizedAccounts: Boolean): GoogleIdTokenCredential? {
        return runCatching {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setAutoSelectEnabled(true)
                // TODO: Uncomment once secrets xml has been added in CI secrets
                // .setServerClientId(context.getString(R.string.serverClientId))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = response.credential
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
