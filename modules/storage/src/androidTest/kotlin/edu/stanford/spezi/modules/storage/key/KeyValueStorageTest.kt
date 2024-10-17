package edu.stanford.spezi.modules.storage.key

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.modules.storage.di.Storage
import kotlinx.serialization.Serializable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class KeyValueStorageTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Storage.Encrypted
    @Inject
    lateinit var encryptedStorage: KeyValueStorage

    @Storage.Unencrypted
    @Inject
    lateinit var unencryptedStorage: KeyValueStorage

    private val key = "test_key"

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        encryptedStorage.clear()
        unencryptedStorage.clear()
    }

    @Test
    fun `it should save and read string data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = "Test String"

        // when
        putString(key, expectedValue)

        // then
        val actualValue = getString(key, "")
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent string data`() = runAllStoragesTest {
        // given
        val default = "default"

        // when
        val actualValue = getString(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete string data correctly`() = runAllStoragesTest {
        // given
        val value = "Test String"
        putString(key, value)

        // when
        delete(key)

        // then
        val actualValue = getString(key, "default")
        assertThat(actualValue).isEqualTo("default")
    }

    @Test
    fun `it should save and read int data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = 42

        // when
        putInt(key, expectedValue)

        // then
        val actualValue = getInt(key, 0)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent int data`() = runAllStoragesTest {
        // given
        val default = 0

        // when
        val actualValue = getInt(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete int data correctly`() = runAllStoragesTest {
        // given
        val value = 42
        putInt(key, value)

        // when
        delete(key)

        // then
        val actualValue = getInt(key, 0)
        assertThat(actualValue).isEqualTo(0)
    }

    @Test
    fun `it should save and read boolean data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = true

        // when
        putBoolean(key, expectedValue)

        // then
        val actualValue = getBoolean(key, false)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent boolean data`() = runAllStoragesTest {
        // given
        val default = false

        // when
        val actualValue = getBoolean(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete boolean data correctly`() = runAllStoragesTest {
        // given
        putBoolean(key, true)

        // when
        delete(key)

        // then
        val actualValue = getBoolean(key, false)
        assertThat(actualValue).isEqualTo(false)
    }

    @Test
    fun `it should save and read float data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = 3.14f

        // when
        putFloat(key, expectedValue)

        // then
        val actualValue = getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent float data`() = runAllStoragesTest {
        // given
        val default = 0f

        // when
        val actualValue = getFloat(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete float data correctly`() = runAllStoragesTest {
        // given
        val value = 3.14f
        putFloat(key, value)

        // when
        delete(key)

        // then
        val actualValue = getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(0f)
    }

    @Test
    fun `it should save and read long data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = 123456789L

        // when
        putLong(key, expectedValue)

        // then
        val actualValue = getLong(key, 0L)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent long data`() = runAllStoragesTest {
        // given
        val default = 0L

        // when
        val actualValue = getLong(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete long data correctly`() = runAllStoragesTest {
        // given
        val value = 123456789L
        putLong(key, value)

        // when
        delete(key)

        // then
        val actualValue = getLong(key, 0L)
        assertThat(actualValue).isEqualTo(0L)
    }

    @Test
    fun `it should save and read byte array data correctly`() = runAllStoragesTest {
        // given
        val expectedValue = byteArrayOf(0x01, 0x02, 0x03, 0x04)

        // when
        putByteArray(key, expectedValue)

        // then
        val actualValue = getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent byte array data`() = runAllStoragesTest {
        // given
        val default = byteArrayOf()

        // when
        val actualValue = getByteArray(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should return null when reading non-existent byte array data`() = runAllStoragesTest {
        // given
        clear()

        // when
        val actualValue = getByteArray(key)

        // then
        assertThat(actualValue).isNull()
    }

    @Test
    fun `it should delete byte array data correctly`() = runAllStoragesTest {
        // given
        val value = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        putByteArray(key, value)

        // when
        delete(key)

        // then
        val actualValue = getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(byteArrayOf())
    }

    @Test
    fun `it should handle serializable type correctly`() = runAllStoragesTest {
        // given
        val data = Complex()
        putSerializable(key, data)

        // when
        val contains = getSerializable<Complex>(key) == data
        delete(key)
        val deleted = getSerializable<Complex>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle serializable list read correctly`() = runAllStoragesTest {
        // given
        val data = listOf(Complex())
        putSerializable(key, data)

        // when
        val contains = getSerializableList<Complex>(key) == data
        delete(key)
        val deleted = getSerializable<List<Complex>>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle clear correctly`() = runAllStoragesTest {
        // given
        val initialValue = 1234
        putInt(key, initialValue)

        // when
        clear()

        // then
        assertThat(getInt(key, -1)).isNotEqualTo(initialValue)
    }

    private fun runAllStoragesTest(block: KeyValueStorage.() -> Unit) {
        listOf(encryptedStorage, unencryptedStorage).forEach { block(it) }
    }

    @Serializable
    data class Complex(val id: Int = 1)
}
