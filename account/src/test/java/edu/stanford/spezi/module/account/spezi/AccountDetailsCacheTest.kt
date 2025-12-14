package edu.stanford.spezi.module.account.spezi

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.AccountDetailsCache
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.junit.Test
import java.nio.charset.StandardCharsets

class AccountDetailsCacheTest {
    private val accountId = UUID("b730ebce-e153-44fc-a547-d47ac9c9d190")
    private val localStorage = object : LocalStorage {
        private val storage = mutableMapOf<String, ByteArray>()

        override fun <T : Any> store(
            key: String,
            value: T,
            settings: LocalStorageSetting,
            serializer: SerializationStrategy<T>,
        ) {
            store(key, value, settings) {
                Json.encodeToString(serializer, value).toByteArray(StandardCharsets.UTF_8)
            }
        }

        override fun <T : Any> store(
            key: String,
            value: T,
            settings: LocalStorageSetting,
            encoding: (T) -> ByteArray,
        ) {
            storage[key] = encoding(value)
        }

        override fun <T : Any> read(
            key: String,
            settings: LocalStorageSetting,
            serializer: DeserializationStrategy<T>,
        ): T? {
            return read(key, settings) {
                Json.decodeFromString(serializer, it.toString(StandardCharsets.UTF_8))
            }
        }

        override fun <T : Any> read(
            key: String,
            settings: LocalStorageSetting,
            decoding: (ByteArray) -> T,
        ): T? {
            return storage[key]?.let {
                decoding(it)
            }
        }

        override fun delete(key: String) {
            storage.remove(key)
        }
    }

    @Test
    fun testCache() {
        val cache = AccountDetailsCache(LocalStorageSetting.Unencrypted)
        cache.localStorage = localStorage

        val details = mockAccountDetails(accountId)
        cache.clearEntry(details.accountId)

        val nullEntry = cache.loadEntry(details.accountId, details.keys)
        assertThat(nullEntry).isNull()

        cache.communicateRemoteChanges(details.accountId, details)

        val entry = cache.loadEntry(details.accountId, details.keys)
        assertThat(entry).isNotNull()
        entry?.let { assertAccountDetailsEqual(it, details) }

        cache.purgeMemoryCache(details.accountId)
        val entryFromDisk = cache.loadEntry(details.accountId, details.keys)
        assertThat(entryFromDisk).isNotNull()
        entryFromDisk?.let { assertAccountDetailsEqual(it, details) }

        cache.clearEntry(details.accountId)
        val nullEntry2 = cache.loadEntry(details.accountId, details.keys)
        assertThat(nullEntry2).isNull()
    }

    @Test
    fun testApplyModifications() {
        val cache = AccountDetailsCache(LocalStorageSetting.Unencrypted)
        cache.localStorage = localStorage

        val details = mockAccountDetails(accountId)
        val keys = details.keys
        cache.clearEntry(details.accountId)

        cache.communicateRemoteChanges(details.accountId, details)

        val modified = AccountDetails()
        val removed = AccountDetails()
        modified.userId = "lelandstanford2@stanford.edu"
        removed.password = details.password

        details.userId = modified.userId
        details.password = null
        val modifications = AccountModifications(modified, removed)

        cache.communicateModifications(details.accountId, modifications)

        val localEntry = cache.loadEntry(details.accountId, keys)
        assertThat(localEntry).isNotNull()
        localEntry?.let { assertAccountDetailsEqual(it, details) }

        cache.purgeMemoryCache(details.accountId)
        val diskEntry = cache.loadEntry(details.accountId, keys)
        assertThat(diskEntry).isNotNull()
        diskEntry?.let { assertAccountDetailsEqual(it, details) }

        cache.clearEntry(details.accountId)
    }
}
