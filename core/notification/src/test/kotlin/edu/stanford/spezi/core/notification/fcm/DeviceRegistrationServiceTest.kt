package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.google.common.truth.Truth.assertThat
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.mockTask
import edu.stanford.spezi.modules.storage.key.KeyValueStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeviceRegistrationServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val functions: FirebaseFunctions = mockk(relaxed = true)
    private val firebaseMessaging: FirebaseMessaging = mockk(relaxed = true)
    private val packageManager: PackageManager = mockk(relaxed = true)
    private val packageInfo: PackageInfo = mockk(relaxed = true)
    private val storage: KeyValueStorage = mockk(relaxed = true)
    private val storageKey = "fcm-notification-token"
    private val registerFunction = "registerDevice"
    private val unregisterFunction = "unregisterDevice"

    private lateinit var service: DeviceRegistrationService

    @Before
    fun setup() {
        every { context.packageManager } returns packageManager
        every { context.packageName } returns "edu.stanford.spezi"
        every { packageManager.getPackageInfo("edu.stanford.spezi", 0) } returns packageInfo

        service = DeviceRegistrationServiceImpl(
            context = context,
            functions = functions,
            firebaseMessaging = firebaseMessaging,
            coroutineScope = SpeziTestScope(),
            storage = storage
        )
    }

    @Test
    fun `it should handle registerDevice correctly`() {
        // given
        val token = "test_token"
        every { storage.getString(any()) } returns null
        val map = slot<Map<String, String>>()
        coEvery {
            functions.getHttpsCallable(registerFunction).call(capture(map))
        } returns mockTask(mockk())

        // when
        service.registerDevice(token)

        // then
        coVerify { functions.getHttpsCallable(registerFunction).call(capture(map)) }
        val body = map.captured
        verify { storage.putString(storageKey, token) }
        assertThat(body["notificationToken"]).isEqualTo(token)
    }

    @Test
    fun `it should ignore registerDevice if same token is stored`() {
        // given
        val storedToken = "stored_token"
        every { storage.getString(storageKey) } returns storedToken

        // when
        service.registerDevice(storedToken)

        // then
        coVerify(exactly = 0) { functions.getHttpsCallable(registerFunction).call(any()) }
    }

    @Test
    fun `it should handle unregisterDevice correctly when token is stored`() = runTest {
        // given
        val token = "test_token"
        every { storage.getString(storageKey) } returns token
        val map = slot<Map<String, String>>()
        coEvery {
            functions.getHttpsCallable(unregisterFunction).call(capture(map))
        } returns mockTask(mockk())

        // when
        service.unregisterDevice()

        // then
        val body = map.captured
        assertThat(body["notificationToken"]).isEqualTo(token)
        assertThat(body["platform"]).isEqualTo("Android")
        verify { storage.delete(storageKey) }
    }

    @Test
    fun `unregisterDevice should not call unregisterDevice if no token is stored`() = runTest {
        // given
        every { storage.getString(storageKey) } returns null

        // when
        service.unregisterDevice()

        // then
        coVerify(exactly = 0) { functions.getHttpsCallable(unregisterFunction).call(any()) }
    }

    @Test
    fun `refreshDeviceToken should fetch and register new token`() {
        // given
        val newToken = "new_test_token"
        coEvery { firebaseMessaging.token } returns mockTask(newToken)
        val map = slot<Map<String, String>>()
        coEvery { functions.getHttpsCallable(registerFunction).call(capture(map)) } returns mockTask(mockk())

        // when
        service.refreshDeviceToken()

        // then
        val body = map.captured
        assertThat(body["notificationToken"]).isEqualTo(newToken)
        verify { storage.putString(storageKey, newToken) }
    }
}
