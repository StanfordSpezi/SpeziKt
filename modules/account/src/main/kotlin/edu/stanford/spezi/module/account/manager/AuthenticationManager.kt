package edu.stanford.spezi.module.account.manager

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

internal class AuthenticationManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val credentialManager: CredentialManager,
    @ApplicationContext private val context: Context,
) {
    private val logger by speziLogger()

    suspend fun linkUserToGoogleAccount(
        googleIdToken: String,
        firstName: String,
        lastName: String,
        email: String,
        selectedGender: String,
        dateOfBirth: LocalDate?,
    ): Result<Unit> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            firebaseAuth.signInAnonymously().await()
            val result = getUser().linkWithCredential(credential).await()
            if (result?.user == null) error("Failed to link to google account")
            saveUserData(
                firstName = firstName,
                lastName = lastName,
                email = email,
                selectedGender = selectedGender,
                dateOfBirth = dateOfBirth
            ).getOrThrow()
        }.onFailure { e ->
            logger.e { "Error linking user to google account: ${e.message}" }
        }
    }

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        selectedGender: String,
        dateOfBirth: LocalDate,
    ): Result<Unit> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (result?.user == null) error("Failed sign up with email and password")
            saveUserData(firstName, lastName, email, selectedGender, dateOfBirth).getOrThrow()
        }.onFailure {
            logger.e(it) { "Error signing up with email and password" }
        }
    }

    private suspend fun saveUserData(
        firstName: String,
        lastName: String,
        email: String,
        selectedGender: String,
        dateOfBirth: LocalDate?,
    ): Result<Unit> {
        return runCatching {
            logger.i { "Signing up user" }
            val user = getUser()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()
            user.updateProfile(profileUpdates).await()
            val userMap = hashMapOf(
                "email" to email,
                "firstName" to firstName,
                "lastName" to lastName,
                "gender" to selectedGender,
                "dateOfBirth" to dateOfBirth
            )
            firestore
                .collection("users")
                .document(user.uid)
                .set(userMap)
                .await().let { }
        }.onFailure { e ->
            logger.e { "Error saving user data: ${e.message}" }
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
            logger.i { "Successfully logged in with $email and $password" }
        }.onFailure { e ->
            logger.e { "Error signing in with email and password: ${e.message}" }
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        return runCatching {
            val googleIdTokenCredential = getCredential(filterByAuthorizedAccounts = true) ?: error("Failed to get credential")
            val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            logger.i { "Credential: $credential" }
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
                // TODO: Uncomment once secrets xml has been added in CI secrets
                // .setServerClientId(context.getString(R.string.serverClientId))
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

    private fun getUser(): FirebaseUser {
        return firebaseAuth.currentUser ?: error("Not authenticated")
    }
}
