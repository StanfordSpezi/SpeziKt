package edu.stanford.spezi.modules.storage.secure

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

class SecureStorage(
    @ApplicationContext val context: Context,
) {
    private val provider = "AndroidKeyStore"
    private val keyStore: KeyStore = KeyStore.getInstance(provider).apply { load(null) }
    private val preferences: SharedPreferences = context.getSharedPreferences("Spezi_SecureStoragePrefs", Context.MODE_PRIVATE)

    fun createKey(
        tag: String,
        size: Int = 2048, // TODO: Should we just use RSA here instead of what iOS uses?
    ): KeyPair {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            tag,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(size)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyPairGenerator.initialize(keyGenParameterSpec)
        return keyPairGenerator.genKeyPair()
    }

    fun retrievePrivateKey(tag: String): PrivateKey? {
        return keyStore.getKey(tag, null) as? PrivateKey
    }

    fun retrievePublicKey(tag: String): PublicKey? {
        return keyStore.getCertificate(tag)?.publicKey
    }

    fun deleteKeys(tag: String) {
        keyStore.deleteEntry(tag)
    }

    fun store(
        credentials: Credentials,
        server: String? = null,
    ) {
        preferences.edit {
            putString(sharedPreferencesKey(server, credentials.username), credentials.password)
        }
    }

    fun deleteCredentials(
        username: String,
        server: String? = null,
    ) {
        val key = sharedPreferencesKey(server, username)
        preferences.edit { remove(key) }
    }

    fun deleteAllCredentials(itemTypes: SecureStorageItemTypes) {
        val containsServerCredentials = itemTypes.types.contains(SecureStorageItemType.SERVER_CREDENTIALS)
        val containsNonServerCredentials = itemTypes.types.contains(SecureStorageItemType.NON_SERVER_CREDENTIALS)
        if (containsServerCredentials || containsNonServerCredentials) {
            preferences.edit {
                preferences.all.forEach {
                    if (it.key.startsWith(" ")) { // non-server credential
                        if (containsNonServerCredentials) {
                            remove(it.key)
                        }
                    } else {
                        if (containsServerCredentials) {
                            remove(it.key)
                        }
                    }
                }
            }
        }

        if (itemTypes.types.contains(SecureStorageItemType.KEYS)) {
            for (tag in keyStore.aliases()) {
                keyStore.deleteEntry(tag)
            }
        }
    }

    fun updateCredentials(
        username: String,
        server: String? = null,
        newCredentials: Credentials,
        newServer: String? = null,
    ) {
        deleteCredentials(username, server)
        store(newCredentials, newServer)
    }

    fun retrieveCredentials(
        username: String,
        server: String? = null,
    ): Credentials? {
        val key = sharedPreferencesKey(server, username)
        return preferences.getString(key, null)?.let {
            Credentials(username, it)
        }
    }

    fun retrieveAllCredentials(
        server: String? = null,
    ): List<Credentials> {
        return preferences.all.mapNotNull { entry ->
            val password = server?.let {
                if (entry.key.startsWith("$server ")) {
                    entry.value as? String
                } else {
                    null
                }
            } ?: entry.value as? String

            password?.let {
                val separatorIndex = entry.key.indexOf(" ")
                Credentials(entry.key.drop(separatorIndex + 1), password)
            }
        }
    }

    // TODO: Check for potential key collisions
    private fun sharedPreferencesKey(server: String?, username: String): String =
        "${server ?: ""} $username"
}
