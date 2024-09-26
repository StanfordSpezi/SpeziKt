package edu.stanford.spezi.module.account.firebase.account

import android.accounts.Account
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import edu.stanford.spezi.module.account.account.AccountDetails
import edu.stanford.spezi.module.account.account.AccountModifications
import edu.stanford.spezi.module.account.account.AccountService
import edu.stanford.spezi.module.account.account.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.AccountServiceConfigurationStorage
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FirebaseEmulatorSettings(
    val host: String,
    val port: Int
)

private sealed class InitialUserState {
    data object Unknown: InitialUserState()
    data object NotPresent: InitialUserState()
    data class Present(val incomplete: Boolean): InitialUserState()
}

private sealed class UserChange {
    data class User(val user: FirebaseUser): UserChange()
    data object Removed: UserChange()
}

private data class UserUpdate(
    val change: UserChange,
    var authResult: AuthResult? = null
) {
    companion object {
        val removed = UserUpdate(UserChange.Removed)

        operator fun invoke(authResult: AuthResult): UserUpdate {
            // TODO: Figure out how to deal with this error
            return UserUpdate(UserChange.User(authResult.user ?: throw Error()), authResult)
        }
    }
}

class FirebaseAccountService(
    private val emulatorSettings: FirebaseEmulatorSettings? = null,
    val authProviders: Set<FirebaseAuthProvider>,
) : AccountService {

    override val configuration: AccountServiceConfiguration = AccountServiceConfiguration(
        AccountServiceConfigurationStorage()
    )

    val auth = FirebaseAuth.getInstance()

    @Inject lateinit var account: Account
    @Inject lateinit var externalStorage: ExternalAccountStorage

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
                throw FirebaseAccountError.NotSignedIn
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
            throw FirebaseAccountError.NotSignedIn
        }
    }

    private fun ensureSignedOutBeforeLogin() {
        auth.currentUser?.let { user ->
            if (!user.isAnonymous) {
                auth.signOut()
            }
        }
    }

    private suspend fun reauthenticateUser(user: FirebaseUser): ReauthenticationOperation {

    }

    private suspend fun reauthenticateUserPassword(user: FirebaseUser): ReauthenticationOperation {

    }

    private suspend fun reauthenticateUserApple(user: FirebaseUser): ReauthenticationOperation {

    }

    private suspend fun updateDisplayName(user: FirebaseUser, name: String) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        ).await()
    }

    private fun notifyUserRemoval() {
        account.removeUserDetails()
    }
}