package edu.stanford.spezi.foundation

import kotlin.reflect.KClass

/**
 * A [KnowledgeSource] serves as a typed key for a [ValueRepository] and is anchored to a given [RepositoryAnchor].
 *
 * @param Anchor the type of the [RepositoryAnchor] this [KnowledgeSource] provides values for.
 * @param Value the type of the values provided by this [KnowledgeSource].
 */
interface KnowledgeSource<Anchor : RepositoryAnchor, Value : Any>

/**
 * A [KnowledgeSource] that provides a default value of type [Value] if no value is present /
 * previously registered in the [ValueRepository].
 *
 * Since [ValueRepository] APIs expect a [KnowledgeSource] type as a key, e.g. a [KClass],
 * it must be able to internally initialize the source instance. Therefore a
 * [DefaultProvidingKnowledgeSource] must either be a Kotlin object or have a companion object,
 * so that the [defaultValue] can be accessed without requiring an instance.
 *
 * Example:
 *
 * ```kotlin
 * // An object implementing DefaultProvidingKnowledgeSource
 * data object UserNameKey : DefaultProvidingKnowledgeSource<MyRepositoryAnchor, String> {
 *     override val defaultValue: String = "Spezi User"
 * }
 *
 * // A data class implementing DefaultProvidingKnowledgeSource via delegation to its companion object
 * data class User(
 *     val id: String,
 *     val name: String,
 * ) : DefaultProvidingKnowledgeSource<MyRepositoryAnchor, User> by Companion {
 *
 *     companion object : DefaultProvidingKnowledgeSource<MyRepositoryAnchor, User> {
 *         override val defaultValue: User = User(id = "0", name = "Default User")
 *     }
 * }
 *
 * // accessing values from a ValueRepository
 * val repository = ValueRepository<MyRepositoryAnchor>()
 *
 * val defaultUserName: String = repository[UserNameKey::class] // returns "Spezi User"
 * val defaultUser: User = repository[User::class] // returns User(id = "0", name = "Default User")
 * ```
 *
 * @param Anchor the type of the [RepositoryAnchor] this [KnowledgeSource] provides values for.
 * @param Value the type of the values provided by this [KnowledgeSource].
 */
interface DefaultProvidingKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : KnowledgeSource<Anchor, Value> {
    val defaultValue: Value
}

/**
 * Defines the storage policy for a [ComputedKnowledgeSource] or
 * [OptionalComputedKnowledgeSource].
 *
 * - [AlwaysCompute] means the value is recomputed every time it is requested.
 * - [Store] means the computed value may be stored in the repository after computation.
 */
sealed interface ComputedKnowledgeSourceStoragePolicy {
    data object AlwaysCompute : ComputedKnowledgeSourceStoragePolicy
    data object Store : ComputedKnowledgeSourceStoragePolicy
}

/**
 * A [KnowledgeSource] that computes its value based on the current state of a [ValueRepository].
 *
 * This is a shared base interface for computed knowledge sources that either always return a value
 * or may return `null`.
 */
sealed interface SomeComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> :
    KnowledgeSource<Anchor, Value> {
    val storagePolicy: ComputedKnowledgeSourceStoragePolicy
}

/**
 * A [KnowledgeSource] that computes its value based on the current state of a [ValueRepository]
 * and always returns a non-null value.
 *
 * Since [ValueRepository] APIs expect a [KnowledgeSource] type as a key, e.g. a [KClass],
 * it must be able to internally initialize the source instance. Therefore a
 * [ComputedKnowledgeSource] must either be a Kotlin object or have a companion object,
 * so that the compute function can be accessed without requiring an instance.
 *
 * Example:
 *
 * ```kotlin
 * // A simple default source
 * data object UserListKey : DefaultProvidingKnowledgeSource<MyRepositoryAnchor, List<User>> {
 *     override val defaultValue: List<User> = emptyList()
 * }
 *
 * // An object implementing ComputedKnowledgeSource
 * data object UserCountKey : ComputedKnowledgeSource<MyRepositoryAnchor, Int> {
 *     override val storagePolicy =  ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
 *
 *     override fun compute(repository: ValueRepository<MyRepositoryAnchor>): Int {
 *         return repository[UserListKey::class].size
 *     }
 * }
 *
 * // A data class implementing ComputedKnowledgeSource via delegation
 * data class UserStatistics(
 *     val userCount: Int,
 * ) : ComputedKnowledgeSource<MyRepositoryAnchor, UserStatistics> by Companion {
 *
 *     companion object : ComputedKnowledgeSource<MyRepositoryAnchor, UserStatistics> {
 *         override val storagePolicy = ComputedKnowledgeSourceStoragePolicy.Store
 *
 *         override fun compute(repository: ValueRepository<MyRepositoryAnchor>): UserStatistics {
 *             val count = repository[UserCountKey::class]
 *             return UserStatistics(userCount = count)
 *         }
 *     }
 * }
 *
 * val repository = ValueRepository<MyRepositoryAnchor>()
 *
 * val count: Int = repository[UserCountKey::class]
 * val statistics: UserStatistics = repository[UserStatistics::class]
 * ```
 *
 * @param Anchor the type of the [RepositoryAnchor] this [KnowledgeSource] provides values for.
 * @param Value the type of the values provided by this [KnowledgeSource].
 */
interface ComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> : SomeComputedKnowledgeSource<Anchor, Value> {
    fun compute(repository: ValueRepository<Anchor>): Value
}

/**
 * A [KnowledgeSource] that computes its value based on the current state of a [ValueRepository]
 * and may return `null`.
 *
 * This is useful when a value can only be computed if certain inputs are present in the repository.
 *
 * As with [ComputedKnowledgeSource], implementations must be either a Kotlin object or provide
 * a companion object that implements this interface.
 *
 * Example:
 *
 * ```kotlin
 * data object OptionalUserKey : OptionalComputedKnowledgeSource<MyRepositoryAnchor, User> {
 *     override val storagePolicy = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
 *
 *     override fun compute(repository: ValueRepository<MyRepositoryAnchor>): User? {
 *         val users = repository[UserListKey::class]
 *         return users.firstOrNull()
 *     }
 * }
 *
 * val repository = ValueRepository<MyRepositoryAnchor>()
 *
 * val firstUser: User? = repository[OptionalUserKey::class]
 * ```
 *
 * @param Anchor the type of the [RepositoryAnchor] this [KnowledgeSource] provides values for.
 * @param Value the type of the values provided by this [KnowledgeSource].
 */
interface OptionalComputedKnowledgeSource<Anchor : RepositoryAnchor, Value : Any> :
    SomeComputedKnowledgeSource<Anchor, Value> {
    fun compute(repository: ValueRepository<Anchor>): Value?
}

/**
 * Type alias representing the [KClass] of a [KnowledgeSource].
 */
typealias KnowledgeSourceType<Anchor, Value> = KClass<out KnowledgeSource<Anchor, Value>>

/**
 * Type alias representing the [KClass] of a [DefaultProvidingKnowledgeSource].
 */
typealias DefaultProvidingKnowledgeSourceType<Anchor, Value> = KClass<out DefaultProvidingKnowledgeSource<Anchor, Value>>

/**
 * Type alias representing the [KClass] of a [ComputedKnowledgeSource].
 */
typealias ComputedKnowledgeSourceType<Anchor, Value> = KClass<out ComputedKnowledgeSource<Anchor, Value>>

/**
 * Type alias representing the [KClass] of an [OptionalComputedKnowledgeSource].
 */
typealias OptionalComputedKnowledgeSourceType<Anchor, Value> = KClass<out OptionalComputedKnowledgeSource<Anchor, Value>>
