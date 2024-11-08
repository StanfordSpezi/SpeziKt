package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.value
import java.util.Date

private object AccountDateOfBirthKey : AccountKey<Date> {
    override val uuid = UUID()
    override val identifier = "dateOfBirth"
    override val name = StringResource("UAP_SIGNUP_DATE_OF_BIRTH_TITLE")
    override val category = AccountKeyCategory.personalDetails
    override val initialValue: InitialValue<Date> = InitialValue.Empty(Date())

    @Composable
    override fun DisplayComposable(value: Date) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun EntryComposable(state: MutableState<Date>) {
        TODO("Not yet implemented")
    }
}

val AccountKeys.dateOfBirth: AccountKey<Date>
    get() = AccountDateOfBirthKey

var AccountDetails.dateOfBirth: Date
    get() = this.storage[AccountKeys.dateOfBirth] ?: AccountKeys.dateOfBirth.initialValue.value
    set(value) { this.storage[AccountKeys.dateOfBirth] = value }
