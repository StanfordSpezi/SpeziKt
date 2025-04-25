package edu.stanford.spezi.modules.utils.extensions

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json

inline fun <reified T> SavedStateHandle.decode(key: String): T {
    val jsonString =
        this.get<String>(key) ?: throw IllegalArgumentException("Argument not found")
    return Json.decodeFromString(jsonString)
}
