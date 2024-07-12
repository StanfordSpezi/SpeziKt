package edu.stanford.spezi.module.account.register

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.CredentialRegisterManagerAuth
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RegisterViewModelTest {

    private lateinit var registerViewModel: RegisterViewModel
    private val credentialRegisterManagerAuth: CredentialRegisterManagerAuth = mockk(relaxed = true)
    private val messageNotifier: MessageNotifier = mockk(relaxed = true)
    private val accountEvents: AccountEvents = mockk(relaxed = true)
    private val validator: RegisterFormValidator = mockk(relaxed = true)

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(
            credentialRegisterManagerAuth,
            messageNotifier,
            accountEvents,
            validator
        )
    }

    @Test
    fun `given TextFieldUpdate action when onAction is called then update RegisterUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.TextFieldUpdate("Andrea", TextFieldType.FIRST_NAME)

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.firstName.value).isEqualTo("Andrea")
        }

    @Test
    fun `given DateFieldUpdate action when onAction is called then update RegisterUiState`() =
        runTestUnconfined {
            // Given
            val action = Action.DateFieldUpdate(LocalDate.of(1993, 6, 30))

            // When
            registerViewModel.onAction(action)

            // Then
            val uiState = registerViewModel.uiState.value
            assertThat(uiState.dateOfBirth).isEqualTo(LocalDate.of(1993, 6, 30))
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
}
