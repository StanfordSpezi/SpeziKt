package edu.stanford.spezi.module.account.firebase.account.models

import com.google.firebase.auth.FirebaseUser

internal sealed interface UserChange {
    data class User(val user: FirebaseUser) : UserChange
    data object Removed : UserChange
}
