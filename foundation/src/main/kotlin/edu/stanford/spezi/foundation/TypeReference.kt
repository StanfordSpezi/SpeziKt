package edu.stanford.spezi.foundation

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A class that represents a type reference capturing generic types at runtime.
 *
 * Instances of this type can be build via [typeReference] function.
 */
sealed interface TypeReference<out T : Any> {
    val type: Type
}

/**
 * Returns the simple name of the type represented by this [TypeReference].
 */
val <T : Any> TypeReference<T>.simpleTypeName: String
    get() = type.typeName.substringAfterLast('.')

/**
 * Base class for [TypeReference]s. Instances of this class are created and returned
 * via the [typeReference] function.
 *
 */
@PublishedApi
internal abstract class TypeReferenceImpl<T : Any> : TypeReference<T> {
    override val type: Type by lazy {
        (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments?.firstOrNull()
            ?: Any::class.java
    }

    override fun equals(other: Any?) = other is TypeReference<*> && type == other.type
    override fun hashCode() = type.hashCode()
    override fun toString(): String = type.typeName
}

/**
 * Creates a [TypeReference] for the specified type.
 *
 * Example usage:
 * ```
 * val typeRef = typeReference<List<String>>()
 * ```
 *
 * @param T The type to capture.
 */
inline fun <reified T : Any> typeReference(): TypeReference<T> = object : TypeReferenceImpl<T>() {}
