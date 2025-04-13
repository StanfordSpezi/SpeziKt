package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.testing.mockTask
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PhoneNumberServiceTest {
    private val userId = "some-uid"
    private val phoneNumber = "+12345678"
    private val firebaseFunctions: FirebaseFunctions = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns userId
    }
    private val context: Context = mockk()
    private val json =
        """
        {
          "emojis": { "US": "🇺🇸" }
        }
        """.trimIndent()

    private val service = PhoneNumberService(
        context = context,
        firebaseFunctions = firebaseFunctions,
        userSessionManager = userSessionManager,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Before
    fun setUp() {
        val resources: Resources = mockk()
        every { context.resources } returns resources
        every { resources.openRawResource(R.raw.country_emojis) } returns json.byteInputStream()
    }

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

    @Test
    fun `it should handle delete phone number success correctly`() = runTest {
        // given
        val data = mapOf(
            "phoneNumber" to phoneNumber,
            "userId" to userId
        )
        val httpsCallableReference: HttpsCallableReference = mockk()
        val httpCallableResult: HttpsCallableResult = mockk()
        every { httpCallableResult.data } returns data
        every {
            firebaseFunctions.getHttpsCallable("deletePhoneNumber")
        } returns httpsCallableReference
        every { httpsCallableReference.call(data) } returns mockTask(httpCallableResult)

        // when
        val result = service.deletePhoneNumber(phoneNumber)

        // then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should handle delete phone number failure correctly`() = runTest {
        // given
        val exception = Exception("Function call failed")
        every {
            firebaseFunctions.getHttpsCallable("deletePhoneNumber")
        } throws exception

        // when
        val result = service.deletePhoneNumber(phoneNumber)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `it should return country codes with emoji from JSON and globe for unknown`() = runTest {
        // when
        val result = service.getAllCountryCodes()

        // then
        val unitedStates = result.firstOrNull { it.iso == "US" }
        assertThat(unitedStates).isNotNull()
        assertThat(unitedStates?.emoji).isEqualTo("🇺🇸")

        val other = result.firstOrNull { it.iso != "US" }
        assertThat(other).isNotNull()
        assertThat(other?.emoji).isEqualTo("🌐")
    }

    @Test
    fun `it should format a valid phone number correctly`() {
        // given
        val rawPhoneNumber = "+14155552671"
        val expectedFormatted = "+1 415-555-2671"

        // when
        val formatted = service.format(rawPhoneNumber)

        // then
        assertThat(formatted).isEqualTo(expectedFormatted)
    }

    @Test
    fun `it should validate a correct phone number`() {
        // given
        val validNumber = "+491734758775"
        val iso = "DE"

        // when
        val isValid = service.isPhoneNumberValid(validNumber, iso)

        // then
        assertThat(isValid).isTrue()
    }
}
