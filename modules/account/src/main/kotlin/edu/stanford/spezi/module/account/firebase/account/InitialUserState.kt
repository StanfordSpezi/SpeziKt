package edu.stanford.spezi.module.account.firebase.account

import com.google.firebase.auth.FirebaseUser

internal sealed interface InitialUserState {
    data object Unknown : InitialUserState
    data object NotPresent : InitialUserState
    data class Present(val incomplete: Boolean) : InitialUserState

    fun canSkipStateChange(user: FirebaseUser?): Boolean =
        when (this) {
            is Unknown -> false
            is NotPresent -> user == null
            is Present -> !incomplete && user != null
        }
}