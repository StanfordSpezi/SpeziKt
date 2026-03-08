package edu.stanford.spezi.account

import edu.stanford.spezi.core.Module

/**
 * Configuration for encoding and decoding account details, allowing for custom identifier mappings.
 *
 * By default, SpeziAccount uses the internal key identifier of the [AnyAccountKey]
 * as the identifier for encoding and decoding account details. However, this configuration allows you to
 * provide a custom mapping between internal key identifiers and stored identifiers.
 *
 * @property identifierMapping A map that defines how to translate between internal key identifiers and stored identifiers.
 */
data class AccountDetailsCodecConfig(
    private val identifierMapping: Map<String, String> = emptyMap(),
) : Module {

    fun encodingIdentifier(key: AnyAccountKey): String {
        return identifierMapping[key.identifier] ?: key.identifier
    }

    fun resolveDecodingKey(
        storedIdentifier: String,
        requestedKeys: Set<AnyAccountKey>,
    ): AnyAccountKey? {
        return requestedKeys.firstOrNull {
            val mapped = identifierMapping[it.identifier] ?: it.identifier
            mapped == storedIdentifier
        }
    }

    companion object {
        operator fun invoke(builder: MutableMap<AnyAccountKey, String>.() -> Unit): AccountDetailsCodecConfig {
            val rawMapping = mutableMapOf<AnyAccountKey, String>().apply(builder)

            val blankKeys = rawMapping.entries.filter { it.value.isBlank() }.map { it.key.identifier }
            require(blankKeys.isEmpty()) {
                "Custom stored identifiers must not be blank. Affected keys: ${blankKeys.joinToString()}"
            }

            val duplicates = rawMapping.values.groupBy { it }.filter { it.value.size > 1 }.keys
            require(duplicates.isEmpty()) {
                "Duplicate custom stored identifiers detected: ${duplicates.joinToString()}. " +
                    "Each key must map to a unique stored identifier."
            }

            val mapping = rawMapping.mapKeys { it.key.identifier }
            return AccountDetailsCodecConfig(mapping)
        }
    }
}
