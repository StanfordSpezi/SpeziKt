package edu.stanford.spezi.module.account.cred.manager

import android.content.Context
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CredentialManagerAuth @Inject constructor(
    private val credentialManager: CredentialManager,
    private val firebaseAuthManager: FirebaseAuthManager,
    private val navigator: Navigator,
) : SignInHandler {
    private val logger by speziLogger()

    override fun handleSignIn(
        context: Context,
        scope: CoroutineScope,
        updateUiState: (GoogleIdTokenCredential) -> Unit,
    ) {
        scope.launch {
            var result = getCredential(context, true)
            if (result == null) {
                logger.i { "No authorized accounts found" }
                result = getCredential(context, false)
            }
            result?.let { handleSignIn(it, updateUiState) }
        }
    }

    private suspend fun getCredential(
        context: Context,
        filterByAuthorizedAccounts: Boolean,
    ): GetCredentialResponse? {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(context.getString(R.string.serverClientId))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            credentialManager.getCredential(
                request = request,
                context = context
            )
        } catch (e: GetCredentialException) {
            logger.e { "Error getting credential: ${e.message}" }
            null
        }
    }

    private suspend fun handleSignIn(
        result: GetCredentialResponse,
        updateUiState: (GoogleIdTokenCredential) -> Unit,
    ) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        logger.i { "Display Name: ${googleIdTokenCredential.displayName}" }
                        logger.i { "Phone Number: ${googleIdTokenCredential.phoneNumber}" }
                        logger.i { "Family Name: ${googleIdTokenCredential.familyName}" }
                        updateUiState(googleIdTokenCredential)

                        if (isNewUser(googleIdTokenCredential)) {
                            logger.i { "New user detected" }
                            if (firebaseAuthManager.linkUserToGoogleAccount(googleIdTokenCredential.idToken)) {
                                navigator.navigateTo(
                                    AccountNavigationEvent.RegisterScreen(
                                        isGoogleSignIn = true
                                    )
                                )
                            } else {
                                logger.e { "Failed to link user to google account" }
                            }
                        } else {
                            logger.i { "Existing user detected" }
                            navigator.navigateTo(DefaultNavigationEvent.BluetoothScreen)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        logger.e { "Received an invalid google id token response" }
                    }
                } else {
                    logger.e { "Unexpected type of credential" }
                }
            }

            else -> {
                logger.e { "Unexpected type of credential" }
            }
        }
    }

    private suspend fun isNewUser(credential: GoogleIdTokenCredential): Boolean {
        logger.i { "Checking if new user" }
        firebaseAuthManager.checkIfNewUser(credential).let {
            return it == null
        }
    }

    suspend fun registerPassword(username: String, password: String, context: Context) {
        val createPasswordRequest =
            CreatePasswordRequest(id = username, password = password)

        val createCredential = credentialManager.createCredential(
            context,
            createPasswordRequest
        )
        handleCreateCredential(createCredential)
    }

    private fun handleCreateCredential(createCredential: CreateCredentialResponse) {
        when (createCredential.type) {
            PasswordCredential.TYPE_PASSWORD_CREDENTIAL -> {
                logger.i { "Successfully created credential" }
            }

            else -> {
                logger.e { "Unexpected type of credential" }
            }
        }
    }
}
