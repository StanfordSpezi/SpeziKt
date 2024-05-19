package edu.stanford.spezikt.core.bluetooth.domain

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class PermissionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun isPermissionGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}