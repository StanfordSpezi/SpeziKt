package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.spezi.Module
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

data class AccountDetailsSerializer(
    val keys: List<AccountKey<*>>,
    val identifierMapping: Map<String, AccountKey<*>>
) : KSerializer<AccountDetails> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("edu.stanford.spezi.account.details-cache", SerialKind.CONTEXTUAL) {
        TODO("Figure out what this is...")
    }

    override fun deserialize(decoder: Decoder): AccountDetails {
        val input = decoder as? JsonDecoder ?: throw SerializationException("This class can be decoded only by Json format")
        val tree = input.decodeJsonElement() as? JsonObject ?: throw SerializationException("Expected JsonObject")

        TODO("Visitor not implemented yet")
    }

    override fun serialize(encoder: Encoder, value: AccountDetails) {
        val output = encoder as? JsonEncoder ?: throw SerializationException("This class can be encoded only by Json format")
        val tree = mutableMapOf<String, JsonObject>()
        TODO("Visitor not implemented yet")
        // output.encodeJsonElement(JsonObject(tree))
    }
}

class AccountDetailsCache(
    private val storageSettings: LocalStorageSetting = LocalStorageSetting.EncryptedUsingKeyStore
) : Module {
    private val logger by speziLogger()

    private val localCache = mutableMapOf<String, AccountDetails>()

    @Inject private lateinit var localStorage: LocalStorage

    fun loadEntry(accountId: String, keys: List<AccountKey<*>>): AccountDetails? {
        localCache[accountId]?.let {
            return it
        }

        localStorage.read("edu.stanford.spezi.account.details-cache", storageSettings) {
            val details = AccountDetails()
            for (key in keys) {
                key.identifier
            }
        }

        return null // TODO: lead from persistency as well
    }

    fun clearEntry(accountId: String) {
        localCache.remove(accountId)

        // TODO: Delete persistence as well
    }

    internal fun purgeMemoryCache(accountId: String) {
        localCache.remove(accountId)
    }

    fun communicateModifications(accountId: String, modifications: AccountModifications) {
        val details = AccountDetails()
        localCache[accountId]?.let {
            // TODO("AccountDetails.add(contentsOf:) missing!")
        }
        // TODO("AccountDetails.add(contentsOf:merge:) missing!")
        // TODO("AccountDetails.removeAll() missing!")

        communicateRemoteChanges(accountId, details)
    }

    fun communicateRemoteChanges(accountId: String, details: AccountDetails) {
        localCache[accountId] = details

        // TODO: Persistent store missing
    }
}
