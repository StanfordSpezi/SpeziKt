package edu.stanford.spezi.foundation

private const val RADIX = 16

/**
 * A instance holder class serving as an object identifier for that instance reference.
 *
 * This class is used to create a unique identifier for an object based on its memory address, relying on
 * Java's `[System.identityHashCode]`. It provides a way to compare object references and equality check is
 * implemented using reference equality (`===`).
 *
 * This class is useful for creating unique identifiers for objects based on their memory address.
 *
 * @param T The type of the object to identify.
 * @property ref The object reference.
 */
class ObjectIdentifier<T : Any>(val ref: T) {
    private val identityHash by lazy { System.identityHashCode(ref) }

    override fun equals(other: Any?): Boolean {
        return other is ObjectIdentifier<*> && this.ref === other.ref
    }

    override fun hashCode(): Int = identityHash

    override fun toString(): String {
        return "ObjectIdentifier(${ref::class.simpleName}@${identityHash.toString(RADIX)})"
    }
}
