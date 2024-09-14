package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeviceRegistrationServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val functions: FirebaseFunctions = mockk(relaxed = true)
    private val messageTokenService = mockk<MessageTokenService>(relaxed = true)

    private val service: DeviceRegistrationService =
        DeviceRegistrationService(
            context = context,
            functions = functions,
            messageTokenService = messageTokenService,
        )

    @Test
    fun `registerDevice should not call register device function when notification token is null`() =
        runTest {
            // Arrange
            val exception = Exception("Failed to fetch token")
            coEvery { messageTokenService.getNotificationToken() } throws exception

            // When
            service.registerDevice()

            // Then
            coVerify(
                atMost = 0,
                atLeast = 0,
            ) { functions.getHttpsCallable(any()) }
        }
}
