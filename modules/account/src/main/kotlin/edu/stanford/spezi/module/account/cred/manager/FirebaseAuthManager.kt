package edu.stanford.spezi.module.account.cred.manager

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    private val logger by speziLogger()

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result -> cont.resume(result) { } }
            addOnFailureListener { exception -> cont.resumeWithException(exception) }
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkIfNewUser(account: GoogleIdTokenCredential): AuthResult? {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        }.onFailure { logger.e { it.message.toString() } }
            .onSuccess { logger.i { "User already exists" } }
            .getOrNull()
    }

    suspend fun linkUserToGoogleAccount(googleIdToken: String): Boolean {
        return try {
            val credential =
                GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = firebaseAuth.currentUser?.linkWithCredential(credential)?.await()
            result?.user != null
        } catch (e: Exception) {
            false
        }
    }

    fun getUserData(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun signUp(
        email: String,
        password: String?,
        firstName: String,
        lastName: String,
        selectedGender: String,
        dateOfBirth: LocalDate?,
    ) {
        runCatching {
            if (password != null) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                // TODO add credential to CredentialManager
            }

            logger.i { "Signing up user" }
            getUserData()?.let { user ->
                val userMap = hashMapOf(
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "gender" to selectedGender,
                    "dateOfBirth" to dateOfBirth
                )
                firestore.collection("users").document(user.uid).set(userMap).await()
            }
        }.onSuccess {
            logger.i { "User signed up successfully" }
        }.onFailure { e ->
            logger.e { "Error signing up user: ${e.message}" }
        }
    }
}
