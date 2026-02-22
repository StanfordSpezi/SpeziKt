package edu.stanford.spezi.account

/**
 * Represents the initial value state of an account-related value.
 *
 * This sealed interface has two implementations:
 * - [Empty]: Indicates that there is no initial value set.
 * - [Default]: Indicates that a default initial value is provided.
 *
 * @param T The type of the value.
 */
sealed interface InitialValue<T> {
    val value: T?

    data class Empty<T>(override val value: T) : InitialValue<T>
    data class Default<T>(override val value: T) : InitialValue<T>
    data class Optional<T>(override val value: T?) : InitialValue<T>

    companion object {
        /**
         * Creates an [InitialValue] with no initial value set.
         *
         * @param T The type of the value.
         * @return An [InitialValue] instance representing an empty state.
         */
        fun <T> empty(value: T) = Empty(value)

        /**
         * Creates an [InitialValue] with a default initial value provided.
         *
         * @param T The type of the value.
         * @return An [InitialValue] instance representing a default state.
         */
        fun <T> default(value: T) = Default(value)

        /**
         * Creates an [InitialValue] that can be null.
         *
         * @param T The type of the value.
         * @return An [InitialValue] instance representing an optional state.
         */
        fun <T> nullable(value: T? = null) = Optional(value)

        /**
         * Empty string initial value.
         */
        val string = empty(value = "")

        /**
         * Empty integer initial value.
         */
        val integer = empty(value = 0)

        /**
         * Default boolean initial value.
         */
        val boolean = default(false)

        /**
         * Empty double initial values.
         */
        val double = empty(value = 0.0)

        /**
         * Empty float initial value.
         */
        val float = empty(value = 0f)

        /**
         * Empty list initial value.
         */
        fun <T> list() = empty(emptyList<T>())

        /**
         * Empty set initial value.
         */
        fun <T> set() = empty(value = emptySet<T>())
    }
}
