package edu.stanford.spezi.module.account.account.value

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.userId
import kotlinx.serialization.KSerializer

interface AccountKey<Value : Any> : KnowledgeSource<AccountAnchor, Value> {
    val identifier: String
    val name: StringResource
    val category: AccountKeyCategory get() = AccountKeyCategory.other
    val initialValue: InitialValue<Value>
    val serializer: KSerializer<Value>

    @Composable
    fun DisplayComposable(value: Value)

    @Composable
    fun EntryComposable(value: Value, onValueChanged: (Value) -> Unit)

    @Composable
    fun EntryComposable(state: MutableState<Value>) {
        EntryComposable(state.value, onValueChanged = { state.value = it })
    }
}

internal val <Value : Any> AccountKey<Value>.isRequired: Boolean
    get() = this is RequiredAccountKey<Value>

internal val <Value : Any> AccountKey<Value>.isHiddenCredential: Boolean
    get() = this == AccountKeys.accountId || this == AccountKeys.userId

interface ComputedAccountKey<Value : Any> : AccountKey<Value>, ComputedKnowledgeSource<AccountAnchor, Value>

interface OptionalComputedAccountKey<Value : Any> : AccountKey<Value>, OptionalComputedKnowledgeSource<AccountAnchor, Value>

interface RequiredAccountKey<Value : Any> : AccountKey<Value>, DefaultProvidingKnowledgeSource<AccountAnchor, Value> {
    override val defaultValue: Value
        get() = error("""
           The required account key was tried to be accessed but wasn't provided! 
            Please verify your `AccountConfiguration` or the implementation of your `AccountService`.
        """.trimIndent())
}
