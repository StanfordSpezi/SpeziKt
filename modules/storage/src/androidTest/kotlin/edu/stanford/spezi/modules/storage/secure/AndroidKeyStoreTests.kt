package edu.stanford.spezi.modules.storage.secure

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AndroidKeyStoreTests {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var androidKeyStore: AndroidKeyStore

    private val keyName = "TestKey"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `it should create keys correctly`() {
        // given
        val key = androidKeyStore.createKey(keyName).getOrThrow()

        // when
        val keyPair = androidKeyStore.retrieveKeyPair(keyName)
        val privateKey = androidKeyStore.retrievePrivateKey(keyName)
        val publicKey = androidKeyStore.retrievePublicKey(keyName)
        val aliases = androidKeyStore.aliases()

        // then
        assertThat(privateKey).isEqualTo(key.private)
        assertThat(privateKey).isEqualTo(keyPair?.private)
        assertThat(publicKey).isEqualTo(key.public)
        assertThat(publicKey).isEqualTo(keyPair?.public)
        assertThat(aliases).contains(keyName)
    }

    @Test
    fun `it should handle key deletion correctly`() {
        // given
        androidKeyStore.createKey(keyName)

        // when
        androidKeyStore.deleteEntry(keyName)
        val privateKey = androidKeyStore.retrievePrivateKey(keyName)
        val publicKey = androidKeyStore.retrievePublicKey(keyName)
        val aliases = androidKeyStore.aliases()

        // then
        assertThat(privateKey).isNull()
        assertThat(publicKey).isNull()
        assertThat(aliases).doesNotContain(keyName)
    }

    @Test
    fun `it should handle clear correctly`() {
        // given
        androidKeyStore.createKey(keyName)

        // when
        androidKeyStore.clear()

        // then
        assertThat(androidKeyStore.retrieveKeyPair(keyName)).isNull()
    }
}
