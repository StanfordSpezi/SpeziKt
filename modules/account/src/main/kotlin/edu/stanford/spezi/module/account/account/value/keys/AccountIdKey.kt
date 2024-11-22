package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.RequiredAccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import kotlinx.serialization.builtins.serializer

private object AccountIdKey : RequiredAccountKey<String> {
    override val identifier = "id"
    override val name = StringResource("ACCOUNT_ID")
    override val category = AccountKeyCategory.credentials
    override val initialValue: InitialValue<String> = InitialValue.Empty("")
    override val serializer = String.serializer()

    @Composable
    override fun DisplayComposable(value: String) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun EntryComposable(value: String, onValueChanged: (String) -> Unit) {
        TODO("Not yet implemented")
    }
}

val AccountKeys.accountId: AccountKey<String>
    get() = AccountIdKey

var AccountDetails.accountId: String
    get() = this.storage[AccountKeys.accountId] ?: TODO()
    set(value) { this.storage[AccountKeys.accountId] = value }
