@file:Suppress("LongParameterList")

package edu.stanford.spezi.module.account.manager

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.register.GenderIdentity
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

internal class AuthenticationManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    private val logger by speziLogger()

    suspend fun linkUserToGoogleAccount(googleIdToken: String): Boolean {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            firebaseAuth.signInAnonymously().await()
            val result = firebaseAuth.currentUser?.linkWithCredential(credential)?.await()
            result?.user != null
        }.onFailure { e ->
            logger.e { "Error linking user to google account: ${e.message}" }
        }.getOrDefault(false)
    }

    private fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private suspend fun setUserProfile(firstName: String, lastName: String, email: String) {
        val user = firebaseAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$firstName $lastName")
            .build()

        user?.updateEmail(email)?.await()

        user?.updateProfile(profileUpdates)?.await()
    }

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        selectedGender: String,
        dateOfBirth: LocalDate,
    ): Result<Boolean> {
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // TODO check if we can use credential here
            //  val credential = EmailAuthProvider.getCredential(email, password)
            //  firebaseAuth.currentUser?.linkWithCredential(credential)?.await()
            saveUserData(firstName, lastName, email, selectedGender, dateOfBirth)
            true
        }
    }

    suspend fun saveUserData(
        firstName: String,
        lastName: String,
        email: String,
        selectedGender: String,
        dateOfBirth: LocalDate,
    ): Boolean {
        return runCatching {
            setUserProfile(firstName, lastName, email)
            logger.i { "Signing up user" }
            val localDateTime = dateOfBirth.atStartOfDay()
            val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
            val birthDayTimestamp = Timestamp(instant.epochSecond, instant.nano)

            getUser()?.let { user ->
                val userMap = hashMapOf(
                    "GenderIdentityKey" to GenderIdentity.fromDisplayName(selectedGender).databaseName,
                    "DateOfBirthKey" to birthDayTimestamp
                )
                firestore.collection("users").document(user.uid).set(userMap).await()
            }
        }.onFailure { e ->
            logger.e { "Error saving user data: ${e.message}" }
        }.isSuccess
    }

    suspend fun sendForgotPasswordEmail(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Unit
        }.onFailure { e ->
            logger.e { "Error sending forgot password email: ${e.message}" }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        }.onFailure { e ->
            logger.e { "Error signing in with email and password: ${e.message}" }
        }.getOrDefault(false)
    }

    suspend fun signInWithGoogle(idToken: String): Result<Boolean> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            logger.i { "Credential: $credential" }
            val result = firebaseAuth.signInWithCredential(credential).await()
            logger.i { "Result: $result" }
            result.user != null
        }.onFailure {
            logger.e { "Error signing in with google: ${it.message}" }
        }
    }
}
