package edu.stanford.spezi.module.account.cred.manager

import android.content.Context
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope

interface SignInHandler {
    fun handleSignIn(
        context: Context,
        scope: CoroutineScope,
        updateUiState: (GoogleIdTokenCredential) -> Unit
    )
}