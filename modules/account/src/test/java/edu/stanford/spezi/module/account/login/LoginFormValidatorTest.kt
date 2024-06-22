package edu.stanford.spezi.module.account.login

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.module.account.register.FieldState
import org.junit.Test

class LoginFormValidatorTest {

    private val loginFormValidator = LoginFormValidator()

    @Test
    fun `given valid email when isValidEmail is called then return Valid`() {
        // Given
        val validEmail = "test@test.com"

        // When
        val result = loginFormValidator.isValidEmail(validEmail)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid email when isValidEmail is called then return Invalid`() {
        // Given
        val invalidEmail = "invalidEmail"

        // When
        val result = loginFormValidator.isValidEmail(invalidEmail)

        // Then
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `given valid password when isValidPassword is called then return Valid`() {
        // Given
        val validPassword = "password123"

        // When
        val result = loginFormValidator.isValidPassword(validPassword)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid password when isValidPassword is called then return Invalid`() {
        // Given
        val invalidPassword = "pass"

        // When
        val result = loginFormValidator.isValidPassword(invalidPassword)

        // Then
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `given valid form when isFormValid is called then return true`() {
        // Given
        val validEmail = "test@test.com"
        val validPassword = "password123"
        val uiState = UiState(
            email = FieldState(value = validEmail),
            password = FieldState(value = validPassword)
        )

        // When
        val result = loginFormValidator.isFormValid(uiState)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given invalid form when isFormValid is called then return false`() {
        // Given
        val invalidEmail = "invalidEmail"
        val invalidPassword = "pass"
        val uiState = UiState(
            email = FieldState(value = invalidEmail),
            password = FieldState(value = invalidPassword)
        )

        // When
        val result = loginFormValidator.isFormValid(uiState)

        // Then
        assertThat(result).isFalse()
    }
}
