package edu.stanford.spezi.module.account.firebase.storage

import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

data class EmulatorSettings(
    val host: String,
    val port: Int,
)

data class FirebaseStorageConfiguration @Inject constructor(
    private val emulatorSettings: EmulatorSettings? = null,
) {
    // TODO: Figure out how to specify dependency on ConfigureFirebaseApp

    init {
        emulatorSettings?.let {
            FirebaseStorage.getInstance().useEmulator(it.host, it.port)
        }
    }
}
