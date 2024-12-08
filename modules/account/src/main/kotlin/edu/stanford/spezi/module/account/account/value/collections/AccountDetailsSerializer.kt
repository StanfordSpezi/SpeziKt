package edu.stanford.spezi.module.account.account.value.collections

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.keys.decodingErrors
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

class AccountDetailsEmptySerializer : AccountDetailsSerializer(keys = emptyList())

fun AccountDetails.serializer(
    keys: List<AccountKey<*>>,
    identifierMapping: Map<String, AccountKey<*>>? = null,
    requireAllKeys: Boolean = false,
    lazyDecoding: Boolean = false,
) = AccountDetailsSerializer(
    keys = keys,
    identifierMapping = identifierMapping,
    requireAllKeys = requireAllKeys,
    lazyDecoding = lazyDecoding,
)

@OptIn(ExperimentalSerializationApi::class)
open class AccountDetailsSerializer(
    private val keys: List<AccountKey<*>>,
    private val identifierMapping: Map<String, AccountKey<*>>? = null,
    private val requireAllKeys: Boolean = false,
    private val lazyDecoding: Boolean = false,
) : KSerializer<AccountDetails> {
    override val descriptor: SerialDescriptor = descriptor(null)

    override fun deserialize(decoder: Decoder): AccountDetails {
        val value = AccountDetails()
        val decodingErrors = mutableListOf<Pair<AccountKey<*>, Throwable>>()
        val descriptor = descriptor
        decoder.decodeStructure(descriptor) {
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break
                val key = keys[index]
                @Suppress("detekt:TooGenericExceptionCaught")
                try {
                    key.deserialize(descriptor, this, index, value)
                } catch (error: Throwable) {
                    decodingErrors.add(Pair(key, error))
                    if (!lazyDecoding) throw error
                }
            }
        }
        value.decodingErrors = decodingErrors
        return value
    }

    override fun serialize(encoder: Encoder, value: AccountDetails) {
        val descriptor = descriptor(value)
        encoder.encodeStructure(descriptor) {
            value.keys.onEachIndexed { index, key ->
                key.serialize(descriptor, this, index, value)
            }
        }
    }

    private fun descriptor(value: AccountDetails?) = buildClassSerialDescriptor("AccountDetailsSerializer") {
        for (key in value?.keys ?: keys) {
            val identifier = identifierMapping?.entries?.firstOrNull { it.value == key }?.key
            element(identifier ?: key.identifier, key.serializer.descriptor, isOptional = !requireAllKeys)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun <Value : Any> AccountKey<Value>.deserialize(
    descriptor: SerialDescriptor,
    decoder: CompositeDecoder,
    index: Int,
    details: AccountDetails,
) {
    details[this] = decoder.decodeNullableSerializableElement(descriptor, index, serializer)
}

private fun <Value : Any> AccountKey<Value>.serialize(
    descriptor: SerialDescriptor,
    encoder: CompositeEncoder,
    index: Int,
    details: AccountDetails,
) {
    details[this]?.let {
        encoder.encodeSerializableElement(descriptor, index, serializer, it)
    }
}
