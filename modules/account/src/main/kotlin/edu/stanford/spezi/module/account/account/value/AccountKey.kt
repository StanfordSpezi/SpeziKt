package edu.stanford.spezi.module.account.account.value

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountStorage
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.foundation.knowledgesource.OptionalComputedKnowledgeSource

interface AccountKey<Value : Any> : KnowledgeSource<AccountAnchor, Value> {
    val identifier: String
    val name: StringResource
    val category: AccountKeyCategory get() = AccountKeyCategory.other
    val initialValue: InitialValue<Value>

    @Composable
    fun DisplayComposable(value: Value)

    @Composable
    fun EntryComposable(state: MutableState<Value>)
}

internal val <Value : Any> AccountKey<Value>.isHiddenCredential: Boolean
    get() = this == AccountKeys.accountId || this == AccountKeys.userId

interface ComputedAccountKey<
    Value : Any,
    StoragePolicy : ComputedKnowledgeSourceStoragePolicy,
    > : ComputedKnowledgeSource<AccountAnchor, Value, StoragePolicy, AccountStorage>, AccountKey<Value>

interface OptionalComputedAccountKey<
    Value : Any,
    StoragePolicy : ComputedKnowledgeSourceStoragePolicy,
    > : AccountKey<Value>,
    OptionalComputedKnowledgeSource<AccountAnchor, Value, StoragePolicy, AccountStorage>

interface RequiredAccountKey<Value : Any> : AccountKey<Value>, DefaultProvidingKnowledgeSource<AccountAnchor, Value> {
    override val defaultValue: Value
        get() = TODO("""
           The required account key was tried to be accessed but wasn't provided! 
            Please verify your `AccountConfiguration` or the implementation of your `AccountService`.
        """.trimIndent())
}
