package edu.stanford.speziclaid.serialization

import com.google.protobuf.Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ProtoSerializer<T : Message>(
    private val defaultInstance: T
) : KSerializer<T> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ProtoMessage", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: T) {
        val byteArray = value.toByteArray() // Convert Protobuf message to byte array
        encoder.encodeSerializableValue(ByteArraySerializer(), byteArray) // Use ByteArraySerializer to encode the byte array
    }

    override fun deserialize(decoder: Decoder): T {
        val byteArray = decoder.decodeSerializableValue(ByteArraySerializer()) // Decode as byte array
        return defaultInstance.parserForType.parseFrom(byteArray) as T // Parse Protobuf message from byte array
    }
}