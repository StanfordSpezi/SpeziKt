package edu.stanford.spezi.account.firebase

/**
 * Configuration for connecting [FirebaseAccountService] to a Firebase Authentication emulator.
 *
 * This is primarily intended for **local development and testing** environments where
 * Firebase services are emulated locally instead of connecting to the production backend.
 *
 * When configured, the account service will connect to the Firebase Authentication emulator
 * running at the specified [host] and [port].
 *
 * ## Example
 *
 * Configure the emulator only for debug builds:
 *
 * ```kotlin
 * override val configuration = Configuration {
 *
 *     val emulator = if (BuildConfig.DEBUG) {
 *         FirebaseEmulatorSettings(
 *             host = "10.0.2.2", // Android emulator -> localhost
 *             port = 9099
 *         )
 *     } else {
 *         null
 *     }
 *
 *     accountConfiguration(
 *         service = FirebaseAccountService(
 *             emulatorSettings = emulator
 *         ),
 *         storageProvider = FirestoreAccountStorage(collectionPath = "users")
 *     )
 * }
 * ```
 *
 * For a physical device connected to your development machine you may instead use:
 *
 * ```kotlin
 * FirebaseEmulatorSettings(
 *     host = "localhost",
 *     port = 9099
 * )
 * ```
 *
 * @property host The host where the Firebase emulator is running.
 * @property port The port used by the Firebase emulator.
 *
 * @see FirebaseAccountService
 */
data class FirebaseEmulatorSettings(
    val host: String,
    val port: Int,
)
