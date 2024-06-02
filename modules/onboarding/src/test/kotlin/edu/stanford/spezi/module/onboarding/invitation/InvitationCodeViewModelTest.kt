package edu.stanford.spezi.module.onboarding.invitation

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class InvitationCodeViewModelTest {

    private val invitationAuthManager: InvitationAuthManager = mockk(relaxed = true)
    private val invitationCodeRepository: InvitationCodeRepository = mockk(relaxed = true)
    private lateinit var invitationCodeViewModel: InvitationCodeViewModel

    @Before
    fun setup() {
        invitationCodeViewModel =
            InvitationCodeViewModel(invitationAuthManager, invitationCodeRepository)
    }

    @Test
    fun `it should update invitationCode on UpdateInvitationCode action`() = runTestUnconfined {
        // given
        val newInvitationCode = "newCode"
        val action = Action.UpdateInvitationCode(newInvitationCode)

        // when
        invitationCodeViewModel.onAction(action)

        // then
        val uiState = invitationCodeViewModel.uiState.first()
        assertThat(newInvitationCode).isEqualTo(uiState.invitationCode)
    }

    @Test
    fun `it should clear error on ClearError action`() = runTestUnconfined {
        // given
        val action = Action.ClearError

        // when
        invitationCodeViewModel.onAction(action)

        // then
        val uiState = invitationCodeViewModel.uiState.first()
        assertThat(uiState.error).isNull()
    }

    @Test
    fun `it should invoke gotAnAccountAction on AlreadyHasAccountPressed action`() =
        runTestUnconfined {
            // given
            val action = Action.AlreadyHasAccountPressed

            // when
            invitationCodeViewModel.onAction(action)

            // then
            verify { invitationCodeRepository.getScreenData().gotAnAccountAction() }
        }
}