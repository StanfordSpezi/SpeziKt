package edu.stanford.spezi.modules.storage.local

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.modules.storage.secure.AndroidKeyStore
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
    lateinit var androidKeyStore: AndroidKeyStore

    private val key = "storage_key"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() = runTestUnconfined {
        localStorage.delete(key = key)
        androidKeyStore.deleteEntry(key)
    }

    @Serializable
    data class Letter(val greeting: String)

    @Test
    fun `it should handle complex type correctly`() = runTestUnconfined {
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
        val storedLetter = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer<Letter>(),
        )

        // then
        assertThat(letter).isEqualTo(storedLetter)
    }

    @Test
    fun `it should handle custom coding correctly`() = runTestUnconfined {
        // given
        val greeting = "Hello Paul 👋 ${"🚀".repeat(Random.nextInt(10))}"
        val letter = Letter(greeting = greeting)
        val serializer = Letter.serializer()
        localStorage.store(
            key = key,
            value = letter,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            encoding = { Json.encodeToString(serializer, it).toByteArray(StandardCharsets.UTF_8) }
        )

        // when
        val storedLetter = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            decoding = { data ->
                Json.decodeFromString(serializer, String(data, StandardCharsets.UTF_8))
            }
        )

        // then
        assertThat(letter).isEqualTo(storedLetter)
    }

    @Test
    fun `it should handle deletion of complex type correctly`() = runTestUnconfined {
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
        val afterDelete = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer<Letter>(),
        )
        assertThat(letter).isEqualTo(storedLetter)
        assertThat(afterDelete).isNull()
    }

    @Test
    fun `it should handle list of complex types correctly`() = runTestUnconfined {
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
    fun `it should handle primitive type correctly`() = runTestUnconfined {
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
    fun `it should handle Unencrypted setting correctly`() = runTestUnconfined {
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
    fun `it should handle Encrypted with custom key pair setting correctly`() = runTestUnconfined {
        // given
        val value = UUID().toString()
        val keyPair = androidKeyStore.createKey(key).getOrThrow()
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
