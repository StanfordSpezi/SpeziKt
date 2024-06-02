package edu.stanford.spezi.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    data object RegisterScreen : Routes()

    @Serializable
    data class LoginScreen(val isAlreadyRegistered: @Serializable Boolean) : Routes()

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