package edu.stanford.spezi.storage.local

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.foundation.UUID
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.random.Random

@HiltAndroidTest
class LocalStorageTests {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var localStorage: LocalStorage

    @Inject
    lateinit var keyStorage: KeyStorage

    private val key = "storage_key"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() = runTest {
        localStorage.delete(key = key)
        keyStorage.deleteAll()
    }

    @Serializable
    data class Letter(val greeting: String)

    @Test
    fun `it should return a correct instance via companion object default initializer`() {
        // given
        val storage = LocalStorage.create(InstrumentationRegistry.getInstrumentation().targetContext)

        // then
        assertThat(storage).isInstanceOf(LocalStorageImpl::class.java)
    }

    @Test
    fun `it should handle complex type correctly`() = runTest {
        // given
        val greeting = "Hello Paul 👋 ${"🚀".repeat(Random.nextInt(10))}"
        val letter = Letter(greeting = greeting)
        localStorage.store(
            key = key,
            value = letter,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer()
        )

        // when
        val storedLetter = localStorage.read<Letter>(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer(),
        )

        // then
        assertThat(letter).isEqualTo(storedLetter)
    }

    @Test
    fun `it should handle custom coding correctly`() = runTest {
        // given
        val greeting = "Hello Paul 👋 ${"🚀".repeat(Random.nextInt(10))}"
        val letter = Letter(greeting = greeting)
        localStorage.store(
            key = key,
            value = letter,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            encoding = { Json.encodeToString(serializer(), it).toByteArray(StandardCharsets.UTF_8) }
        )

        // when
        val storedLetter = localStorage.read<Letter>(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            decoding = { Json.decodeFromString(serializer(), String(it, StandardCharsets.UTF_8)) }
        )

        // then
        assertThat(letter).isEqualTo(storedLetter)
    }

    @Test
    fun `it should handle deletion of complex type correctly`() = runTest {
        // given
        val greeting = "Hello Paul 👋 ${"🚀".repeat(Random.nextInt(10))}"
        val letter = Letter(greeting = greeting)
        localStorage.store(
            key = key,
            value = letter,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer()
        )
        val storedLetter = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer<Letter>(),
        )

        // when
        localStorage.delete(key)

        // then
        val afterDelete = localStorage.read<Letter>(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer(),
        )
        assertThat(letter).isEqualTo(storedLetter)
        assertThat(afterDelete).isNull()
    }

    @Test
    fun `it should handle list of complex types correctly`() = runTest {
        // given
        val greeting = "Hello Paul 👋 ${"🚀".repeat(Random.nextInt(10))}"
        val letters = listOf(Letter(greeting = greeting))
        localStorage.store(
            key = key,
            value = letters,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = ListSerializer(serializer())
        )

        // when
        val storedLetters = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = ListSerializer(serializer<Letter>()),
        )

        // then
        assertThat(letters).isEqualTo(storedLetters)
    }

    @Test
    fun `it should handle primitive type correctly`() = runTest {
        // given
        val value = Random.nextBoolean()
        localStorage.store(
            key = key,
            value = value,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer()
        )

        // when
        val storedValue = localStorage.read<Boolean>(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer()
        )

        // then
        assertThat(value).isEqualTo(storedValue)
    }

    @Test
    fun `it should handle Unencrypted setting correctly`() = runTest {
        // given
        val value = UUID().toString()
        localStorage.store(
            key = key,
            value = value,
            settings = LocalStorageSetting.Unencrypted,
            serializer = serializer()
        )

        // when
        val storedValue = localStorage.read<String>(
            key = key,
            settings = LocalStorageSetting.Unencrypted,
            serializer = serializer()
        )

        // then
        assertThat(value).isEqualTo(storedValue)
    }

    @Test
    fun `it should handle Encrypted with custom key pair setting correctly`() = runTest {
        // given
        val value = UUID().toString()
        val androidKeyStoreKey = "androidKeyStoreKey"
        val keyPair = keyStorage.create(androidKeyStoreKey).getOrThrow()
        localStorage.store(
            key = key,
            value = value,
            settings = LocalStorageSetting.Encrypted(keyPair),
            serializer = serializer()
        )

        // when
        val storedValue = localStorage.read<String>(
            key = key,
            settings = LocalStorageSetting.Encrypted(keyPair),
            serializer = serializer()
        )

        // then
        assertThat(value).isEqualTo(storedValue)
    }
}
