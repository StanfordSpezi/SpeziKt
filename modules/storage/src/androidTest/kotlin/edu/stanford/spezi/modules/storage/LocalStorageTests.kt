package edu.stanford.spezi.modules.storage

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.random.Random

@HiltAndroidTest
class LocalStorageTests {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var localStorage: LocalStorage

    private val key = "storage_key"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        localStorage.delete(key = key)
    }

    @Serializable
    data class Letter(val greeting: String)

    @Test
    fun testComplexType() {
        var greeting = "Hello Paul 👋"
        for (index in 0..Random.nextInt(10)) {
            greeting += "🚀"
        }
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

        assertThat(letter).isEqualTo(storedLetter)
    }

    @Test
    fun testListComplexType() {
        var greeting = "Hello Paul 👋"
        for (index in 0..Random.nextInt(10)) {
            greeting += "🚀"
        }
        val letters = listOf(Letter(greeting = greeting))
        localStorage.store(
            key = key,
            value = letters,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = ListSerializer(serializer())
        )
        val storedLetters = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = ListSerializer(serializer<Letter>()),
        )

        assertThat(letters).isEqualTo(storedLetters)
    }

    @Test
    fun testPrimitive() {
        val value = Random.nextBoolean()

        localStorage.store(
            key = key,
            value = value,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer()
        )

        val storedValue = localStorage.read(
            key = key,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = serializer<Boolean>()
        )

        assertThat(value).isEqualTo(storedValue)
    }
}
