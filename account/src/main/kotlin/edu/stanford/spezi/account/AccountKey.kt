package edu.stanford.spezi.account

import edu.stanford.spezi.foundation.KnowledgeSource
import edu.stanford.spezi.ui.StringResource
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * A key representing a specific piece of account-related data.
 *
 * @param V The type of data associated with this account key.
 */
interface AccountKey<V : Any> : KnowledgeSource<AccountAnchor, V> {

    /**
     * The unique identifier for this account key.
     */
    val identifier: String

    /**
     * The display name for this account key.
     */
    val name: StringResource

    /**
     * The category this account key belongs to, defaulting to [AccountKeyCategory.Other]
     */
    val category: AccountKeyCategory get() = AccountKeyCategory.Other

    /**
     * Additional options for this account key, defaulting to [AccountKeyOptions.Default]
     */
    val options: AccountKeyOptions get() = AccountKeyOptions.Default

    /**
     * The serializer used for serializing and deserializing the data associated with this account key.
     */
    val serializer: KSerializer<V>

    /**
     * The initial value provider for this account key.
     */
    val initialValue: InitialValue<V>

    /**
     * The composable used to display the data associated with this account key if options indicated [AccountKeyOptions.Display]
     */
    val display: DataDisplayComposable<V>?

    /**
     * The composable used to enter or edit the data associated with this account key if options indicated [AccountKeyOptions.Mutable]
     */
    val entry: DataEntryComposable<V>?
}

/**
 * Determine if the key is required
 */
val AnyAccountKey.isRequired: Boolean
    get() = this is RequiredAccountKey

/**
 * Determine if the key is a hidden credential ([AccountKeys.accountId] or [AccountKeys.userId])
 */
val AnyAccountKey.isHiddenCredential: Boolean
    get() = this == AccountKeys.accountId || this == AccountKeys.userId

/**
 * A type alias for an AccountKey with any type of data.
 */
typealias AnyAccountKey = AccountKey<*>

/**
 * A type alias for the KClass of an AccountKey with a specific type of data.
 *
 * @param T The type of data associated with the AccountKey.
 */
typealias AccountKeyType<T> = KClass<out AccountKey<T>>

/**
 * A type alias for the KClass of an AccountKey with any type of data.
 */
typealias AnyAccountKeyType = AccountKeyType<Any>

/**
 * A typealias for a set of any account key types.
 */
typealias AccountKeyCollection = Set<AccountKeyType<*>>

/**
 * Creates an [AccountKeyCollection] from a variable number of [AnyAccountKey] instances.
 *
 * @param keys The account keys to include in the collection.
 * @return An [AccountKeyCollection] containing the provided keys.
 */
fun accountKeyCollection(vararg keys: AccountKeyType<*>): AccountKeyCollection = keys.toSet()
