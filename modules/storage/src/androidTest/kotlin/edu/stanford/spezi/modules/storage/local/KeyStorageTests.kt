package edu.stanford.spezi.modules.storage.local

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class KeyStorageTests {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var keyStorage: KeyStorage

    private val keyName = "TestKey"

    @Before
    fun setup() {
        hiltRule.inject()
    }

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
