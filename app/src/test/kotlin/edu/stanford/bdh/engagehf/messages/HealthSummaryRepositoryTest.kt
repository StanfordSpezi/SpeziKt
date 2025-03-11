package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class HealthSummaryRepositoryTest {
    private val uid = "some-uid"
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns uid
    }
    private val firebaseFunctions: FirebaseFunctions = mockk()
    private val ioDispatcher = UnconfinedTestDispatcher()

    private val repository = HealthSummaryRepository(
        userSessionManager = userSessionManager,
        firebaseFunctions = firebaseFunctions,
        ioDispatcher = ioDispatcher
    )

    @Test
    fun `getHealthSummary returns failure when user is not authenticated`() =
        runTestUnconfined {
            // given
            every { userSessionManager.getUserUid() } returns null

            // when
            val result = repository.getHealthSummary()

            // then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
        }

    @Test
    fun `getHealthSummary returns failure when function call fails`() = runTestUnconfined {
        // given
        val exception = Exception("Function call failed")
        coEvery {
            firebaseFunctions.getHttpsCallable(any())
        } throws exception

        // when
        val result = repository.getHealthSummary()

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
