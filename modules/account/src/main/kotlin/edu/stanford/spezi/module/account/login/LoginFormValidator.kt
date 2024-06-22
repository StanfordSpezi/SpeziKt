package edu.stanford.spezi.module.account.login

import edu.stanford.spezi.module.account.register.FormValidator
import javax.inject.Inject

internal class LoginFormValidator @Inject constructor() : FormValidator() {

    fun isFormValid(uiState: UiState): Boolean {
        return isValidEmail(uiState.email.value).isValid &&
            isValidPassword(uiState.password.value).isValid
    }
}
