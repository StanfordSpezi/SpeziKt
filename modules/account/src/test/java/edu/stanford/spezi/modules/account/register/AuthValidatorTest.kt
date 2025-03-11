package edu.stanford.spezi.modules.account.register

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuthValidatorTest {

    private val authValidator = AuthValidator()

    @Test
    fun `isFormValid with valid email and password returns true`() {
        // given
        val email = "test@test.com"
        val password = "password"

        // when
        val result = authValidator.isFormValid(password, email)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `given valid email when isValidEmail is called then return Valid`() {
        // Given
        val validEmail = "test@test.com"

        // When
        val result = authValidator.isValidEmail(validEmail)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid email when isValidEmail is called then return Invalid`() {
        // Given
        val invalidEmail = "invalidEmail"

        // When
        val result = authValidator.isValidEmail(invalidEmail)

        // Then
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `given valid password when isValidPassword is called then return Valid`() {
        // Given
        val validPassword = "password123"

        // When
        val result = authValidator.isValidPassword(validPassword)

        // Then
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `given invalid password when isValidPassword is called then return Invalid`() {
        // Given
        val invalidPassword = "pass"

        // When
        val result = authValidator.isValidPassword(invalidPassword)

        // Then
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `isFormValid with invalid email and valid password returns false`() {
        // given
        val email = "test"
        val password = "password"

        // when
        val result = authValidator.isFormValid(password, email)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isFormValid with valid email and invalid password returns false`() {
        // given
        val email = "test@test.com"
        val password = "pass"

        // when
        val result = authValidator.isFormValid(password, email)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `isFormValid with invalid email and invalid password returns false`() {
        // given
        val email = "test"
        val password = "pass"

        // when
        val result = authValidator.isFormValid(password, email)

        // then
        assertThat(result).isFalse()
    }
}
