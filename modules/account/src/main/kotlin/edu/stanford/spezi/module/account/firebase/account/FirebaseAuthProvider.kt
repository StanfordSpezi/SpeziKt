package edu.stanford.spezi.module.account.firebase.account

enum class FirebaseAuthProvider {
    EMAIL_AND_PASSWORD, SIGN_IN_WITH_GOOGLE, ANONYMOUS
}

typealias FirebaseAuthProviders = Set<FirebaseAuthProvider>
