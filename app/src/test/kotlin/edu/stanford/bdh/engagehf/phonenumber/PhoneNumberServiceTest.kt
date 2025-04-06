package edu.stanford.bdh.engagehf.phonenumber

import com.google.common.truth.Truth.assertThat
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.mockTask
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PhoneNumberServiceTest {
    private val userId = "some-uid"
    private val phoneNumber = "+12345678"
    private val firebaseFunctions: FirebaseFunctions = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns userId
    }

    private val service = PhoneNumberService(
        firebaseFunctions = firebaseFunctions,
        userSessionManager = userSessionManager,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `it should handle start number verification success correctly`() = runTest {
        // given
        val data = mapOf(
            "phoneNumber" to phoneNumber,
            "userId" to userId
        )
        val httpsCallableReference: HttpsCallableReference = mockk()
        val httpCallableResult: HttpsCallableResult = mockk()
        every { httpCallableResult.data } returns data
        every {
            firebaseFunctions.getHttpsCallable("startPhoneNumberVerification")
        } returns httpsCallableReference
        every { httpsCallableReference.call(data) } returns mockTask(httpCallableResult)

        // when
        val result = service.startPhoneNumberVerification(phoneNumber)

        // then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should handle start number verification failure correctly`() = runTest {
        // given
        val exception = Exception("Function call failed")
        every {
            firebaseFunctions.getHttpsCallable("startPhoneNumberVerification")
        } throws exception

        // when
        val result = service.startPhoneNumberVerification(phoneNumber)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `it should handle check number verification success correctly`() = runTest {
        // given
        val code = "123456"
        val data = mapOf(
            "phoneNumber" to phoneNumber,
            "code" to code,
            "userId" to userId
        )
        val httpsCallableReference: HttpsCallableReference = mockk()
        val httpCallableResult: HttpsCallableResult = mockk()
        every { httpCallableResult.data } returns data
        every {
            firebaseFunctions.getHttpsCallable("checkPhoneNumberVerification")
        } returns httpsCallableReference
        every { httpsCallableReference.call(data) } returns mockTask(httpCallableResult)

        // when
        val result = service.checkPhoneNumberVerification(code, phoneNumber)

        // then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should handle check number verification failure correctly`() = runTest {
        // given
        val exception = Exception("Function call failed")
        every {
            firebaseFunctions.getHttpsCallable("checkPhoneNumberVerification")
        } throws exception

        // when
        val result = service.checkPhoneNumberVerification("", phoneNumber)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
