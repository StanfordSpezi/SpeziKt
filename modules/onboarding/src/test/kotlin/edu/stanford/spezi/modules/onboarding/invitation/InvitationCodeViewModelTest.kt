package edu.stanford.spezi.modules.onboarding.invitation

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.account.manager.InvitationAuthManager
import edu.stanford.spezi.modules.onboarding.R
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import edu.stanford.spezi.modules.testing.runTestUnconfined
import edu.stanford.spezi.ui.StringResource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InvitationCodeViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

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
    fun `it should redeem invitation code on RedeemInvitationCode action`() = runTestUnconfined {
        // given
        val action = Action.RedeemInvitationCode

        // when
        invitationCodeViewModel.onAction(action)

        // then
        coVerify { invitationAuthManager.checkInvitationCode(any()) }
    }

    @Test
    fun `it should update error message when redeeming invitation code fails`() =
        runTestUnconfined {
            // given
            val action = Action.RedeemInvitationCode
            coEvery { invitationAuthManager.checkInvitationCode(any()) } returns Result.failure(
                Exception()
            )

            // when
            invitationCodeViewModel.onAction(action)

            // then
            val uiState = invitationCodeViewModel.uiState.first()
            assertThat(uiState.error).isEqualTo(StringResource(R.string.onboarding_invitation_code_error_message))
        }

    @Test
    fun `it should redeem invitation code when redeeming invitation code succeeds`() =
        runTestUnconfined {
            // given
            val action = Action.RedeemInvitationCode
            coEvery { invitationAuthManager.checkInvitationCode(any()) } returns Result.success(Unit)

            // when
            invitationCodeViewModel.onAction(action)

            // then
            coVerify { invitationCodeRepository.getScreenData().redeemAction() }
        }
}
