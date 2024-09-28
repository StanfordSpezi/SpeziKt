package edu.stanford.spezi.module.account.register

import javax.inject.Inject

internal class RegisterFormValidator @Inject constructor() : FormValidator() {

    fun isFormValid(uiState: RegisterUiState): Boolean {
        val passwordConditionSatisfied = {
            isValidPassword(uiState.password.value).isValid
        }
        return isValidEmail(uiState.email.value).isValid &&
            passwordConditionSatisfied()
    }
}
