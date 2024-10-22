package edu.stanford.spezi.module.account.firebase.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfigurationStorage
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.firebase.configuration.ConfigureFirebaseApp
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FirebaseEmulatorSettings(
    val host: String,
    val port: Int
)

class FirebaseAccountService(
    private val emulatorSettings: FirebaseEmulatorSettings? = null,
    val authProviders: FirebaseAuthProviders,
) : AccountService {
    private val logger by speziLogger()

    @Inject private lateinit var configureFirebaseApp: ConfigureFirebaseApp

    @Inject private lateinit var account: Account
    @Inject private lateinit var notifications: AccountNotifications
    @Inject private lateinit var externalStorage: ExternalAccountStorage

    override val configuration: AccountServiceConfiguration = AccountServiceConfiguration(
        AccountServiceConfigurationStorage()
    )

    private val auth get() = FirebaseAuth.getInstance()

    override fun configure() {
        emulatorSettings?.let {
            auth.useEmulator(it.host, it.port)
        }

        auth.addAuthStateListener {
            it.currentUser
        }

        runCatching {
            auth.currentUser?.getIdToken(true)
                ?.addOnFailureListener { notifyUserRemoval() }
        }
    }

    suspend fun signUp(details: AccountDetails) {
        ensureSignedOutBeforeLogin()

        TODO("Missing access of password and userId")

        auth.currentUser?.let {
            if (it.isAnonymous) {
                return@let null
            } else {

            }
        } ?: run {

        }
    }

    suspend fun signUp(credential: OAuthCredential) {

    }

    suspend fun login(userId: String, password: String) {
        ensureSignedOutBeforeLogin()

        auth.signInWithEmailAndPassword(userId, password)
    }

    suspend fun signUpAnonymously() {
        ensureSignedOutBeforeLogin()

        auth.signInAnonymously()
    }

    suspend fun resetPassword(userId: String) {
        auth.sendPasswordResetEmail(userId)
    }

    override suspend fun logout() {
        auth.currentUser?.let {
            auth.signOut()
        } ?: run {
            if (account.signedIn) {
                notifyUserRemoval()
            } else {
                TODO("FirebaseAccountError.NotSignedIn")
            }
        }
    }

    override suspend fun delete() {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccountDetails(modifications: AccountModifications) {
        auth.currentUser?.let {

        } ?: run {
            if (account.signedIn) {
                notifyUserRemoval()
            }
            TODO("FirebaseAccountError.NotSignedIn")
        }
    }

    private fun ensureSignedOutBeforeLogin() {
        auth.currentUser?.let { user ->
            if (!user.isAnonymous) {
                auth.signOut()
            }
        }
    }

    private suspend fun updateDisplayName(user: FirebaseUser, name: String) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        ).await()
    }

    private fun notifyUserRemoval() {
        TODO()
    }
}