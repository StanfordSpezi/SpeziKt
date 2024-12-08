package edu.stanford.spezi.core.design.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PermissionRequester(
    missingPermissions: List<String>?,
    onGranted: (String) -> Unit,
) {
    val permission = missingPermissions?.firstOrNull() ?: return
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) onGranted(permission) }

    LaunchedEffect(key1 = permission) {
        launcher.launch(permission)
    }
}