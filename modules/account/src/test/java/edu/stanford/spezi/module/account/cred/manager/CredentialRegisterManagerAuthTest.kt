package edu.stanford.spezi.module.account.cred.manager

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CredentialRegisterManagerAuthTest {

    private lateinit var credentialRegisterManagerAuth: CredentialRegisterManagerAuth
    private val firebaseAuthManager: FirebaseAuthManager = mockk()
    private val credentialManager: CredentialManager = mockk()
    private val context: Context = mockk()

    @Before
    fun setUp() {
        credentialRegisterManagerAuth = CredentialRegisterManagerAuth(
            firebaseAuthManager,
            credentialManager,
            context
        )
    }

    @Test
    fun `given valid data when googleSignUp is called then return true`() = runTestUnconfined {
        // Given
        val idToken = "idToken"
        val firstName = "Leland"
        val lastName = "Stanford"
        val email = "Leland.Stanford@example.com"
        val selectedGender = "Male"
        val dateOfBirth = LocalDate.now()

        coEvery { firebaseAuthManager.linkUserToGoogleAccount(idToken) } returns true
        coEvery {
            firebaseAuthManager.saveUserData(
                firstName,
                lastName,
                email,
                selectedGender,
                dateOfBirth
            )
        } returns true

        // When
        val result = credentialRegisterManagerAuth.googleSignUp(
            idToken,
            firstName,
            lastName,
            email,
            selectedGender,
            dateOfBirth
        )

        // Then
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `given valid data when passwordAndEmailSignUp is called then return true`() =
        runTestUnconfined {
            // Given
            val email = "Leland.Stanford@example.com"
            val password = "password123"
            val firstName = "Leland"
            val lastName = "Stanford"
            val selectedGender = "Male"
            val dateOfBirth = LocalDate.now()

            coEvery {
                firebaseAuthManager.signUpWithEmailAndPassword(
                    email,
                    password,
                    firstName,
                    lastName,
                    selectedGender,
                    dateOfBirth
                )
            } returns Result.success(true)

            // When
            val result = credentialRegisterManagerAuth.passwordAndEmailSignUp(
                email,
                password,
                firstName,
                lastName,
                selectedGender,
                dateOfBirth
            )

            // Then
            assertThat(result.getOrNull()).isTrue()
        }
}
