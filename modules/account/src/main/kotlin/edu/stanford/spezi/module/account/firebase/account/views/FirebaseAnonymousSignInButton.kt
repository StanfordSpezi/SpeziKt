package edu.stanford.spezi.module.account.firebase.account.views

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.module.account.account.views.setup.provider.AccountServiceButton
import edu.stanford.spezi.module.account.firebase.account.FirebaseAccountService

val firebaseRed = Color(255, 145, 0)
val firebaseYellow = Color(255, 196, 0)

@Composable
internal fun FirebaseAnonymousSignInButton(
    service: FirebaseAccountService,
) {
    val color =
        if (isSystemInDarkTheme()) {
            firebaseRed
        } else {
            firebaseYellow
        }

    AccountServiceButton(
        onClick = {
            service.signUpAnonymously()
        },
        colors = ButtonDefaults.buttonColors()
            .copy(containerColor = color),
    ) {
    }
}
