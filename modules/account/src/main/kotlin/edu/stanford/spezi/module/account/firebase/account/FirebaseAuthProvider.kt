package edu.stanford.spezi.module.account.firebase.account

import java.util.EnumSet

enum class FirebaseAuthProvider {
    EMAIL_AND_PASSWORD, SIGN_IN_WITH_GOOGLE, ANONYMOUS
}

typealias FirebaseAuthProviders = EnumSet<FirebaseAuthProvider>
