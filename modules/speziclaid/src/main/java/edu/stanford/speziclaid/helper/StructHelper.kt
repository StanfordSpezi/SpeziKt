package edu.stanford.speziclaid.helper

import com.google.protobuf.ListValue
import com.google.protobuf.Struct
import com.google.protobuf.Value

// Helper function to create a Struct in a Kotlin-friendly way
fun structOf(vararg pairs: Pair<String, Any>): Struct {
    val builder = Struct.newBuilder()
    pairs.forEach { (key, value) ->
        builder.putFields(key, value.toProtoValue())
    }
    return builder.build()
}

// Convert Kotlin types to Protobuf Value
fun Any.toProtoValue(): Value {
    return when (this) {
        is String -> Value.newBuilder().setStringValue(this).build()
        is Number -> Value.newBuilder().setNumberValue(this.toDouble()).build()
        is Boolean -> Value.newBuilder().setBoolValue(this).build()
        is Map<*, *> -> { // Convert nested maps to Struct
            val structBuilder = Struct.newBuilder()
            for ((key, nestedValue) in this) {
                if (key is String && nestedValue != null) {
                    structBuilder.putFields(key, nestedValue.toProtoValue())
                }
            }
            Value.newBuilder().setStructValue(structBuilder.build()).build()
        }
        is List<*> -> { // Convert lists to ListValue
            val listBuilder = ListValue.newBuilder()
            this.forEach { item ->
                if (item != null) {
                    listBuilder.addValues(item.toProtoValue())
                }
            }
            Value.newBuilder().setListValue(listBuilder.build()).build()
        }
        else -> throw IllegalArgumentException("Unsupported type: ${this::class}")
    }
}
