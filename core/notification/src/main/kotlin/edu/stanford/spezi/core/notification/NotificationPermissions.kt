package edu.stanford.spezi.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import edu.stanford.spezi.core.utils.BuildInfo
import edu.stanford.spezi.core.utils.PermissionChecker
import javax.inject.Inject

interface NotificationPermissions {
    fun getRequiredPermissions(): Set<String>
}

internal class NotificationPermissionsImpl @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val buildInfo: BuildInfo,
) : NotificationPermissions {

    override fun getRequiredPermissions(): Set<String> {
        return if (buildInfo.getSdkVersion() >= Build.VERSION_CODES.TIRAMISU) {
            @SuppressLint("InlinedApi")
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (permissionChecker.isPermissionGranted(permission)) emptySet() else setOf(permission)
        } else {
            emptySet()
        }
    }
}
