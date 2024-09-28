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

    @Test
    fun `given valid first name when firstnameResult is called then return Valid`() {
        // Given
        val validFirstName = "John"

        // When
        val result = registerFormValidator.firstnameResult(validFirstName)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid first name when firstnameResult is called then return Invalid`() {
        // Given
        val invalidFirstName = ""

        // When
        val result = registerFormValidator.firstnameResult(invalidFirstName)

        // Then
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `given valid last name when lastnameResult is called then return Valid`() {
        // Given
        val validLastName = "Doe"

        // When
        val result = registerFormValidator.lastnameResult(validLastName)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid last name when lastnameResult is called then return Invalid`() {
        // Given
        val invalidLastName = ""

        // When
        val result = registerFormValidator.lastnameResult(invalidLastName)

        // Then
        assertThat(result.isValid).isFalse()
    }
}
