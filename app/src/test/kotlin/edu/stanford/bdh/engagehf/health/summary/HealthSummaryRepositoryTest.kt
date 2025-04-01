package edu.stanford.bdh.engagehf.health.summary

import com.google.common.truth.Truth.assertThat
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.mockTask
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import java.time.Instant

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

    @Test
    fun `getShareHealthSummaryData returns failure when user is not authenticated`() =
        runTestUnconfined {
            // given
            every { userSessionManager.getUserUid() } returns null

            // when
            val result = repository.getShareHealthSummaryData()

            // then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
        }

    @Test
    fun `getShareHealthSummaryData returns failure when function call fails`() = runTestUnconfined {
        // given
        val exception = Exception("Function call failed")
        coEvery {
            firebaseFunctions.getHttpsCallable(any())
        } throws exception

        // when
        val result = repository.getShareHealthSummaryData()

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getShareHealthSummaryData returns failure when function response is invalid`() = runTestUnconfined {
        // given
        val expiresAtString = "2023-05-18T11:15:30.00Z"
        val resultMap = mapOf(
            "url" to "some-url",
            "code" to "some-code",
            "expiresAt" to expiresAtString
        )
        val httpsCallableReference: HttpsCallableReference = mockk()
        val httpCallableResult: HttpsCallableResult = mockk()
        every { httpCallableResult.data } returns resultMap
        every {
            firebaseFunctions.getHttpsCallable("shareHealthSummary")
        } returns httpsCallableReference
        every { httpsCallableReference.call(mapOf("userId" to uid)) } returns mockTask(httpCallableResult)

        // when
        val result = repository.getShareHealthSummaryData().getOrThrow()

        // then
        with(result) {
            assertThat(url).isEqualTo("some-url")
            assertThat(code).isEqualTo("some-code")
            assertThat(expiresAt).isEqualTo(Instant.parse(expiresAtString))
        }
    }
}
