package edu.stanford.bdh.engagehf.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    data class RegisterScreen(
        val registerParams: @Serializable RegisterParams,
    ) : Routes()

    @Serializable
    data class LoginScreen(val isAlreadyRegistered: @Serializable Boolean = true) : Routes()

    @Serializable
    data object SequentialOnboardingScreen : Routes()

    @Serializable
    data object InvitationCodeScreen : Routes()

    @Serializable
    data object OnboardingScreen : Routes()

    @Serializable
    data object ConsentScreen : Routes()

    @Serializable
    data object BluetoothScreen : Routes()
}

@Serializable
data class RegisterParams(val isGoogleSignUp: Boolean, val email: String, val password: String)
