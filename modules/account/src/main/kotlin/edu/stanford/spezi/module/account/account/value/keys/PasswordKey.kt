package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

private object AccountPasswordKey : AccountKey<String> {
    override val identifier = "password"
    override val name = StringResource("UP_PASSWORD")
    override val category = AccountKeyCategory.credentials
    override val initialValue: InitialValue<String> = InitialValue.Empty("")

    @Composable
    override fun DisplayComposable(value: String) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun EntryComposable(state: MutableState<String>) {
        TODO("Not yet implemented")
    }
}

val AccountKeys.password: AccountKey<String>
    get() = AccountPasswordKey

var AccountDetails.password: String?
    get() = this.storage[AccountKeys.password]
    set(value) { this.storage[AccountKeys.password] = value }
