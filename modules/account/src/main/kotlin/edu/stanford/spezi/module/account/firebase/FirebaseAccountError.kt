package edu.stanford.spezi.module.account.firebase

sealed class FirebaseAccountError: Error() {
    data object NotSignedIn: FirebaseAccountError() {
        private fun readResolve(): Any = NotSignedIn
    }
}