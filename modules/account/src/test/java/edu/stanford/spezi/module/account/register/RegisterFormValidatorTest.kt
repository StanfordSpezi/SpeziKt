package edu.stanford.spezi.module.account.register

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegisterFormValidatorTest {

    private val registerFormValidator = RegisterFormValidator()

    @Test
    fun `given valid password when isValidPassword is called then return Valid`() {
        // Given
        val validPassword = "password123"

        // When
        val result = registerFormValidator.isValidPassword(validPassword)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid password when isValidPassword is called then return Invalid`() {
        // Given
        val invalidPassword = "pass"

        // When
        val result = registerFormValidator.isValidPassword(invalidPassword)

        // Then
        assertThat(result.isValid).isFalse()
    }
}
