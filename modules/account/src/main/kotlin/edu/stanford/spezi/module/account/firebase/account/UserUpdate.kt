package edu.stanford.spezi.module.account.firebase.account

import com.google.firebase.auth.AuthResult

internal data class UserUpdate(
    val change: UserChange,
    var authResult: AuthResult? = null
) {
    fun describesSameUpdate(other: UserUpdate): Boolean =
        when (change) {
            is UserChange.User -> when (other.change) {
                is UserChange.User -> other.change.user.uid == change.user.uid
                is UserChange.Removed -> false
            }
            is UserChange.Removed -> when (other.change) {
                is UserChange.User -> false
                is UserChange.Removed -> true
            }
        }

    companion object {
        val removed = UserUpdate(UserChange.Removed)

        operator fun invoke(authResult: AuthResult): UserUpdate =
            UserUpdate(
                // TODO: On iOS, the user property is not optional, so they never resort back to Removed here
                authResult.user?.let { UserChange.User(it) } ?: UserChange.Removed,
                authResult
            )
    }
}