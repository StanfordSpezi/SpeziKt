package edu.stanford.spezi.core.internal

import edu.stanford.spezi.foundation.TypeReference
import edu.stanford.spezi.foundation.simpleTypeName
import edu.stanford.spezi.foundation.typeReference

/**
 * A unified key identifying any registered dependency – a [edu.stanford.spezi.core.Module],
 * a singleton, or a transient factory – in the [DependencyRegistry].
 *
 * Uses [TypeReference] rather than [kotlin.reflect.KClass] so that generic type parameters are
 * preserved at runtime (e.g. `DependencyKey<List<String>>` and `DependencyKey<List<Int>>` are
 * distinct keys).
 *
 * @param type The full [TypeReference] of the dependency, capturing generic arguments.
 * @param identifier An optional string to disambiguate multiple registrations of the same type.
 */
@PublishedApi
internal data class DependencyKey<T : Any>(
    val type: TypeReference<T>,
    val identifier: String?,
) {
    override fun toString(): String {
        val id = if (identifier == null) "" else " (identifier: $identifier)"
        return "Dependency[${type.simpleTypeName}$id]"
    }

    companion object {
        inline operator fun <reified T : Any> invoke(identifier: String? = null) =
            DependencyKey(typeReference<T>(), identifier)
    }
}
