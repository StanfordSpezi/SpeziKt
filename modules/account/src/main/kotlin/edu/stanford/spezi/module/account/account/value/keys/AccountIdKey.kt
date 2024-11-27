package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.RequiredAccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import kotlinx.serialization.builtins.serializer

private object AccountIdKey : RequiredAccountKey<String> {
    override val identifier = "accountId"
    override val name = StringResource("ACCOUNT_ID")
    override val category = AccountKeyCategory.credentials
    override val initialValue: InitialValue<String> = InitialValue.Empty("")
    override val serializer = String.serializer()

    @Composable
    override fun Display(value: String) {
        Text("The internal account identifier is not meant to be user facing!")
    }

    @Composable
    override fun Entry(value: String, onValueChanged: (String) -> Unit) {
        Text("The internal account identifier is meant to be generated!")
    }
}

val AccountKeys.accountId: AccountKey<String>
    get() = AccountIdKey

var AccountDetails.accountId: String
    get() = this.storage[AccountKeys.accountId] ?: error("There is supposed to be an accountId.")
    set(value) { this.storage[AccountKeys.accountId] = value }
