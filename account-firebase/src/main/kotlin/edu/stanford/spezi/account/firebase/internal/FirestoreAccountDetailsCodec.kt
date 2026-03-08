package edu.stanford.spezi.account.firebase.internal

import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountDetailsCodecConfig
import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.keys
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.dependency
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
internal class FirestoreAccountDetailsCodec : Module {
    private val codecConfig by dependency<AccountDetailsCodecConfig>()
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(details: AccountDetails): Map<String, String> {
        return buildMap {
            details.accountKeyTypes.keys().forEach { key ->
                if (key.identifier == AccountKeys.accountId.identifier) return@forEach
                val typedKey = key as AccountKey<Any>
                val value = details.getAnyOrNull(typedKey::class) ?: return@forEach

                put(
                    key = codecConfig.encodingIdentifier(typedKey),
                    value = json.encodeToString(
                        serializer = typedKey.serializer,
                        value = value,
                    )
                )
            }
        }
    }

    fun decode(firestoreData: Map<String, Any?>, requestedKeys: Set<AnyAccountKey>): AccountDetails {
        val details = AccountDetails()
        firestoreData.forEach { (identifier, rawValue) ->
            val key = codecConfig.resolveDecodingKey(identifier, requestedKeys) ?: return@forEach
            val typedKey = key as AccountKey<Any>
            val decoded = runCatching {
                val jsonString = rawValue as? String
                json.decodeFromString(
                    deserializer = typedKey.serializer,
                    string = jsonString.orEmpty(),
                )
            }.getOrNull() ?: return@forEach
            details.setAny(typedKey::class, decoded)
        }
        return details
    }
}
