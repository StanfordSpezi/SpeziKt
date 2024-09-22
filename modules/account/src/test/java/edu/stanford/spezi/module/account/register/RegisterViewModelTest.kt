package edu.stanford.spezi.module.account.register

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.coVerifyNever
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class RegisterViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var registerViewModel: RegisterViewModel
    private val authenticationManager: AuthenticationManager = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: RegisterFormValidator = mockk(relaxed = true)
    private val dateOfBirth = LocalDate.of(1993, 1, 1)

    @Before
    fun setUp() {
        with(validator) {
            every { isFormValid(any()) } returns true
            every { isValidEmail(any()) } returns FormValidator.Result.Valid
            every { isValidPassword(any()) } returns FormValidator.Result.Valid
            every { firstnameResult(any()) } returns FormValidator.Result.Valid
            every { lastnameResult(any()) } returns FormValidator.Result.Valid
            every { isGenderValid(any()) } returns FormValidator.Result.Valid
            every { birthdayResult(any()) } returns FormValidator.Result.Valid
        }
        registerViewModel = RegisterViewModel(
            authenticationManager = authenticationManager,
            messageNotifier = messageNotifier,
            accountEvents = accountEvents,
            validator = validator
        )
    }

    @Test
    fun `it should update first name correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("first name", TextFieldType.FIRST_NAME)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.firstName.value).isEqualTo("first name")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should update last name correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("last name", TextFieldType.LAST_NAME)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.lastName.value).isEqualTo("last name")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should update email correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("email", TextFieldType.EMAIL)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.email.value).isEqualTo("email")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should update password correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("password", TextFieldType.PASSWORD)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.password.value).isEqualTo("password")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should update gender correctly`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("gender", TextFieldType.GENDER)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.selectedGender.value).isEqualTo("gender")
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `given DateFieldUpdate action when onAction is called then update RegisterUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.DateFieldUpdate(dateOfBirth)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.dateOfBirth).isEqualTo(dateOfBirth)
            assertThat(uiState.isRegisterButtonEnabled).isFalse()
        }

    @Test
    fun `it should enable registered button when all fields available`() {
        // given
        val neededActions = buildList {
            TextFieldType.entries.forEach {
                add(Action.TextFieldUpdate("some-value", it))
            }
            add(Action.DateFieldUpdate(dateOfBirth))
        }

        // when
        neededActions.forEach {
            registerViewModel.onAction(it)
        }

        // then
        assertThat(registerViewModel.uiState.value.isRegisterButtonEnabled).isTrue()
    }

    @Test
    fun `given DropdownMenuExpandedUpdate action when onAction is called then update RegisterUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.DropdownMenuExpandedUpdate(true)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.isDropdownMenuExpanded).isTrue()
        }

    @Test
    fun `it should handle on Register for google sign up correctly`() = runTestUnconfined {
        // given
        val displayName = "Display Name"
        val idToken = "idToken"
        val token: GoogleIdTokenCredential = mockk {
            every { this@mockk.displayName } returns displayName
            every { this@mockk.idToken } returns idToken
        }
        coEvery { authenticationManager.getCredential(any()) } returns token
        registerViewModel.onAction(Action.DateFieldUpdate(dateOfBirth))
        registerViewModel.onAction(Action.SetIsGoogleSignUp(true))
        val uiState = registerViewModel.uiState.value
        coEvery {
            authenticationManager.signUpWithGoogleAccount(
                googleIdToken = idToken,
                firstName = uiState.firstName.value,
                lastName = uiState.lastName.value,
                selectedGender = uiState.selectedGender.value,
                dateOfBirth = uiState.dateOfBirth!!,
                email = uiState.email.value,
            )
        } returns Result.success(Unit)

        // when
        registerViewModel.onAction(Action.OnRegisterPressed)

        // then
        coVerify {
            authenticationManager.signUpWithGoogleAccount(
                googleIdToken = idToken,
                firstName = uiState.firstName.value,
                lastName = uiState.lastName.value,
                selectedGender = uiState.selectedGender.value,
                dateOfBirth = uiState.dateOfBirth!!,
                email = uiState.email.value,
            )
        }
        verify { accountEvents.emit(AccountEvents.Event.SignUpSuccess) }
    }

    @Test
    fun `it should handle on Register for google sign up with email correctly`() =
        runTestUnconfined {
            // given
            val displayName = "Display Name"
            val idToken = "idToken"
            val token: GoogleIdTokenCredential = mockk {
                every { this@mockk.displayName } returns displayName
                every { this@mockk.idToken } returns idToken
            }
            coEvery { authenticationManager.getCredential(any()) } returns token
            registerViewModel.onAction(Action.DateFieldUpdate(dateOfBirth))
            registerViewModel.onAction(Action.SetIsGoogleSignUp(false))
            val uiState = registerViewModel.uiState.value
            coEvery {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                    firstName = uiState.firstName.value,
                    lastName = uiState.lastName.value,
                    selectedGender = uiState.selectedGender.value,
                    dateOfBirth = uiState.dateOfBirth!!,
                )
            } returns Result.success(Unit)

            // when
            registerViewModel.onAction(Action.OnRegisterPressed)

            // then
            coVerify {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                    firstName = uiState.firstName.value,
                    lastName = uiState.lastName.value,
                    selectedGender = uiState.selectedGender.value,
                    dateOfBirth = uiState.dateOfBirth!!,
                )
            }
            verify { accountEvents.emit(AccountEvents.Event.SignUpSuccess) }
        }

    @Test
    fun `it should handle on Register for google sign up with email failure correctly`() =
        runTestUnconfined {
            // given
            val displayName = "Display Name"
            val idToken = "idToken"
            val token: GoogleIdTokenCredential = mockk {
                every { this@mockk.displayName } returns displayName
                every { this@mockk.idToken } returns idToken
            }
            coEvery { authenticationManager.getCredential(any()) } returns token
            registerViewModel.onAction(Action.DateFieldUpdate(dateOfBirth))
            registerViewModel.onAction(Action.SetIsGoogleSignUp(false))
            val uiState = registerViewModel.uiState.value
            coEvery {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                    firstName = uiState.firstName.value,
                    lastName = uiState.lastName.value,
                    selectedGender = uiState.selectedGender.value,
                    dateOfBirth = uiState.dateOfBirth!!,
                )
            } returns Result.failure(Exception("Error"))

            // when
            registerViewModel.onAction(Action.OnRegisterPressed)

            // then
            coVerify {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                    firstName = uiState.firstName.value,
                    lastName = uiState.lastName.value,
                    selectedGender = uiState.selectedGender.value,
                    dateOfBirth = uiState.dateOfBirth!!,
                )
            }
            verify { accountEvents.emit(AccountEvents.Event.SignUpFailure) }
            verify { messageNotifier.notify("Failed to sign up") }
        }

    @Test
    fun `it should reevaluate in case form is not valid for google sign up with email failure correctly`() =
        runTestUnconfined {
            // given
            every { validator.isFormValid(any()) } returns false

            // when
            registerViewModel.onAction(Action.OnRegisterPressed)

            // then
            coVerifyNever {
                authenticationManager.signUpWithEmailAndPassword(
                    email = any(),
                    password = any(),
                    firstName = any(),
                    lastName = any(),
                    selectedGender = any(),
                    dateOfBirth = any(),
                )
            }
            val uiState = registerViewModel.uiState.value
            with(validator) {
                verify { isFormValid(uiState) }
                verify { isValidEmail(uiState.email.value) }
                verify { isValidPassword(uiState.password.value) }
                verify { firstnameResult(uiState.firstName.value) }
                verify { lastnameResult(uiState.lastName.value) }
                verify { isGenderValid(uiState.selectedGender.value) }
                verify { birthdayResult(uiState.dateOfBirth) }
            }
        }
}
