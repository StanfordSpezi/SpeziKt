package edu.stanford.spezi.module.account

import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.foundation.RepositoryAnchor
import java.util.UUID

// SpeziAccount

/*
interface KnowledgeSource<Value, Anchor: RepositoryAnchor> {
    val id: UUID
}

typealias AccountKey<Value> = KnowledgeSource<Value, AccountAnchor>

data class OptionalKnowledgeSource<Value>(
    override val id: UUID = UUID()
) : KnowledgeSource<Value>

val accountId = OptionalKnowledgeSource<String>()

val repository = SharedRepository()

data class ComputedKnowledgeSource<Value>(
    override val id: UUID = UUID(),
    val compute: () -> Value
) : KnowledgeSource<Value>

data class SharedRepository(
    private val map: MutableMap<UUID, Any> = mutableMapOf()
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <Value> get(key: KnowledgeSource<Value>): Value? {
        return map[key.id] as? Value
    }

    operator fun <Value> set(key: KnowledgeSource<Value>, value: Value) {
        map[key.id] = value as Any
    }
}
*/

/*

interface AccountKeyInput<Value : Any> {
    val id: String?
    val name: String
    val category: AccountKeyCategory
    val displayView: @Composable (Value) -> Unit
    val entryView: @Composable (MutableState<Value>) -> Unit
}

annotation class AccountValue<Input: AccountKeyInput<*>>

data class NameAccountKeyInput(val input: Unit): AccountKeyInput<String> {
    override val id: String? get() = null
    override val name: String get() = "Name"
    override val category: AccountKeyCategory get() = AccountKeyCategory.name
    override val displayView: @Composable (String) -> Unit get() = { text ->
        Text(text)
    }
    override val entryView: @Composable (MutableState<String>) -> Unit get() = { state ->
        TextField(value = state.value, onValueChange = { state.value = it })
    }
}

@AccountValue<NameAccountKeyInput>
val AccountDetails.name: String? get() = null
 */
