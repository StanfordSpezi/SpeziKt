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

@OptIn(ExperimentalSerializationApi::class)
class AccountDetailsSerializer(
    private val identifierMapping: Map<String, AccountKey<*>>,
    private val requireAllKeys: Boolean = false,
    private val lazyDecoding: Boolean = false,
) : KSerializer<AccountDetails> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AccountDetailsSerializer") {
        for (identifier in identifierMapping) {
            element(identifier.key, identifier.value.serializer.descriptor, isOptional = !requireAllKeys)
        }
    }

    override fun deserialize(decoder: Decoder): AccountDetails {
        val value = AccountDetails()
        val decodingErrors = mutableListOf<Pair<AccountKey<*>, Throwable>>()
        decoder.decodeStructure(descriptor) {
            @Suppress("detekt:TooGenericExceptionCaught")
            identifierMapping.onEachIndexed { index, identifier ->
                try {
                    identifier.value.deserialize(this, index, value)
                } catch (error: Throwable) {
                    decodingErrors.add(Pair(identifier.value, error))
                    if (!lazyDecoding) throw error
                }
            }
        }
        value.decodingErrors = decodingErrors
        return value
    }

    override fun serialize(encoder: Encoder, value: AccountDetails) {
        encoder.encodeStructure(descriptor) {
            identifierMapping.onEachIndexed { index, identifier ->
                identifier.value.serialize(this, index, value)
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun <Value : Any> AccountKey<Value>.deserialize(
    decoder: CompositeDecoder,
    index: Int,
    details: AccountDetails,
) {
    details[this] = decoder.decodeNullableSerializableElement(serializer.descriptor, index, serializer)
}

private fun <Value : Any> AccountKey<Value>.serialize(
    encoder: CompositeEncoder,
    index: Int,
    details: AccountDetails
) {
    details[this]?.let {
        encoder.encodeSerializableElement(serializer.descriptor, index, serializer, it)
    }
}
