package edu.stanford.spezi.module.account.firebase.storage

import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.module.account.spezi.Module

data class EmulatorSettings(
    val host: String,
    val port: Int,
)

class FirebaseStorageConfiguration(
    private val emulatorSettings: EmulatorSettings? = null,
) : Module {
    // TODO: Figure out how to specify dependency on ConfigureFirebaseApp

    override fun configure() {
        emulatorSettings?.let {
            FirebaseStorage.getInstance().useEmulator(it.host, it.port)
        }
    }
}
