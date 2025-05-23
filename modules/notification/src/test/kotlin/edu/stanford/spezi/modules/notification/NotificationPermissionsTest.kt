package edu.stanford.spezi.modules.notification

import android.Manifest
import android.os.Build
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.utils.BuildInfo
import edu.stanford.spezi.modules.utils.PermissionChecker
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class NotificationPermissionsTest {
    private val permissionChecker: PermissionChecker = mockk()
    private val buildInfo: BuildInfo = mockk()
    private val tiramisu = Build.VERSION_CODES.TIRAMISU

    private val notificationPermissions = NotificationPermissionsImpl(
        permissionChecker = permissionChecker,
        buildInfo = buildInfo,
    )

    @Test
    fun `getRequiredPermissions returns empty set below TIRAMISU`() {
        // given
        every { buildInfo.getSdkVersion() } returns tiramisu - 1

        // when
        val requiredPermissions = notificationPermissions.getRequiredPermissions()

        // then
        assertThat(requiredPermissions).isEmpty()
    }

    @Test
    fun `getRequiredPermissions returns POST_NOTIFICATIONS when permission is not granted on TIRAMISU or higher`() {
        // given
        every { buildInfo.getSdkVersion() } returns tiramisu
        every { permissionChecker.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS) } returns false

        // when
        val requiredPermissions = notificationPermissions.getRequiredPermissions()

        // then
        assertThat(requiredPermissions).isEqualTo(setOf(Manifest.permission.POST_NOTIFICATIONS))
    }

    @Test
    fun `getRequiredPermissions returns empty set when permission is granted on TIRAMISU or higher`() {
        // given
        every { buildInfo.getSdkVersion() } returns tiramisu
        every { permissionChecker.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS) } returns true

        // when
        val requiredPermissions = notificationPermissions.getRequiredPermissions()

        // then
        assertThat(requiredPermissions).isEmpty()
    }
}
