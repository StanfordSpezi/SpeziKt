package edu.stanford.spezi.modules.storage.secure

import android.provider.Settings.Secure

sealed class SecureStorageScope {
    data class SecureEnclave(val userPresence: Boolean = false): SecureStorageScope()
    data class Keychain(val userPresence: Boolean = false, val accessGroup: String? = null): SecureStorageScope()
    data class KeychainSynchronizable(val accessGroup: String? = null): SecureStorageScope()

    companion object {
        val secureEnclave = SecureEnclave()
        val keychain = Keychain()
        val keychainSynchronizable = KeychainSynchronizable()
    }

    val identifier: String get() =
        when (this) {
            is Keychain ->
                "keychain.$userPresence" + (accessGroup?.let { ".$it" } ?: "")
            is KeychainSynchronizable ->
                "keychainSynchronizable" + (accessGroup?.let { ".$it" } ?: "")
            is SecureEnclave ->
                "secureEnclave"
        }

    val userPresenceValue: Boolean get() = // TODO: Think about removing "Value" suffix
        when (this) {
            is SecureEnclave -> userPresence
            is Keychain -> userPresence
            is KeychainSynchronizable -> false
        }

    val accessGroupValue: String? get() =
        when (this) {
            is SecureEnclave -> null
            is Keychain -> accessGroup
            is KeychainSynchronizable -> accessGroup
        }

    // TODO: Missing property accessControl
}