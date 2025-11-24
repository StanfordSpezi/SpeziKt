package edu.stanford.spezi.health.internal

import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.PermissionController
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.healthLogger

/**
 * A [Fragment] that handles requesting health permissions
 *
 * This is a simple invisible fragment used internally by the [Health] module to request health permissions and supply permission granted
 * results back to the [Health] module.
 */
internal class HealthPermissionFragment : Fragment() {
    private val logger by healthLogger()
    private val health by dependency<Health>()

    private val launcher: ActivityResultLauncher<Set<String>> = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        logger.i { "Health permissions granted result: $granted" }
        health.onPermissionsGranted(granted = granted)
        detach()
    }

    val permissions
        get() = requireArguments().getStringArrayList(PERMISSIONS_KEY)?.toSet().orEmpty()

    override fun onStart() {
        super.onStart()
        val permissions = permissions
        if (permissions.isEmpty()) {
            logger.w { "Health permission fragment was launched without permissions to request" }
            detach()
            return
        }
        logger.i { "Launching permission request for $permissions" }
        launcher.launch(permissions)
    }

    private fun detach() {
        logger.i { "Detaching permission fragment for $permissions" }
        parentFragmentManager
            .beginTransaction()
            .remove(this)
            .commitAllowingStateLoss()
    }

    companion object {
        private const val TAG = "HealthPermissionFragment"
        private const val PERMISSIONS_KEY = "health.permissions.key"

        fun startPermissionFlow(
            activity: FragmentActivity,
            permissions: Set<String>,
        ) = with(activity.supportFragmentManager) {
            val newPermissions = permissions.toMutableSet()
            val existingFragment = findFragmentByTag(TAG) as? HealthPermissionFragment
            existingFragment?.let {
                newPermissions.addAll(it.permissions)
                beginTransaction()
                    .remove(it)
                    .commitNowAllowingStateLoss()
            }
            val fragment = HealthPermissionFragment().apply {
                arguments = bundleOf(PERMISSIONS_KEY to ArrayList(newPermissions))
            }
            beginTransaction()
                .add(fragment, TAG)
                .commitNowAllowingStateLoss()
        }
    }
}
