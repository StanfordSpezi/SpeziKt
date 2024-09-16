package edu.stanford.spezi.core.notification.fcm

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.google.firebase.functions.FirebaseFunctions
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeviceRegistrationServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val functions: FirebaseFunctions = mockk(relaxed = true)
    private val packageManager: PackageManager = mockk(relaxed = true)
    private val packageInfo: PackageInfo = mockk(relaxed = true)

    private val service: DeviceRegistrationService =
        DeviceRegistrationService(
            context = context,
            functions = functions,
            ioDispatcher = UnconfinedTestDispatcher()
        )

    @Test
    fun `registerDevice should call register device function when called`() =
        runTest {
            // given
            val token = "token"
            packageInfo.versionName = "1.0.0"
            packageInfo.versionCode = 1
            every { context.packageManager } returns packageManager
            every { context.packageName } returns "edu.stanford.spezi"
            every { packageManager.getPackageInfo("edu.stanford.spezi", 0) } returns packageInfo

            // when
            service.registerDevice(token)

            // then
            coVerify { functions.getHttpsCallable("registerDevice").call(any()) }
        }
}
