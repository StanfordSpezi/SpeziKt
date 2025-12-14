package edu.stanford.spezi.core.internal

import edu.stanford.spezi.core.Module
import edu.stanford.spezi.foundation.TypeReference
import edu.stanford.spezi.foundation.typeReference

/**
 * A key for identifying a module or module factory in the [ModuleRegistry].
 *
 * @param identifier Optional identifier for the module distinguish between different instances of the same module type.
 * @param type The type of the module, represented as a [TypeReference].
 */
@PublishedApi
internal data class ModuleKey<M : Module>(val identifier: String?, val type: TypeReference<M>) {
    override fun toString(): String {
        val identifierString = if (identifier == null) "" else " (identifier: $identifier)"
        return "Module[$type$identifierString]"
    }

    companion object {
        /**
         * Creates a [ModuleKey] for the specified module type.
         *
         * @param M Reified type of the module.
         * @param identifier Optional identifier for the module.
         * @return A [ModuleKey] for the specified module type.
         */
        inline operator fun <reified M : Module> invoke(identifier: String? = null): ModuleKey<M> {
            return ModuleKey(identifier = identifier, type = typeReference())
        }
    }
}
