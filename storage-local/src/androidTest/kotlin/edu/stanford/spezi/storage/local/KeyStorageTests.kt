package edu.stanford.spezi.storage.local

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class KeyStorageTests {

    private val keyStorage: KeyStorage = KeyStorageImpl()

    private val keyName = "TestKey"

    @Test
    fun `it should create keys correctly`() {
        // given
        val key = keyStorage.create(keyName).getOrThrow()

        // when
        val keyPair = keyStorage.retrieveKeyPair(keyName)
        val privateKey = keyStorage.retrievePrivateKey(keyName)
        val publicKey = keyStorage.retrievePublicKey(keyName)

        // then
        assertThat(privateKey).isEqualTo(key.private)
        assertThat(privateKey).isEqualTo(keyPair?.private)
        assertThat(publicKey).isEqualTo(key.public)
        assertThat(publicKey).isEqualTo(keyPair?.public)
    }

    @Test
    fun `it should handle key deletion correctly`() {
        // given
        keyStorage.create(keyName)

        // when
        keyStorage.delete(keyName)
        val privateKey = keyStorage.retrievePrivateKey(keyName)
        val publicKey = keyStorage.retrievePublicKey(keyName)

        // then
        assertThat(privateKey).isNull()
        assertThat(publicKey).isNull()
    }

    @Test
    fun `it should handle clear correctly`() {
        // given
        keyStorage.create(keyName)

        // when
        keyStorage.deleteAll()

        // then
        assertThat(keyStorage.retrieveKeyPair(keyName)).isNull()
    }
}
