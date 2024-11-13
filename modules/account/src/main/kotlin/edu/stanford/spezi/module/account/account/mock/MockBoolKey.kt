package edu.stanford.spezi.module.account.account.mock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.views.display.BooleanDisplay
import edu.stanford.spezi.module.account.account.views.entry.BooleanEntry
import kotlinx.serialization.builtins.serializer

private object MockBoolKey : AccountKey<Boolean> {
    override val name = StringResource("Toggle")
    override val identifier = "mockBool"
    override val category = AccountKeyCategory.other
    override val initialValue = InitialValue.Default(false)
    override val serializer = Boolean.serializer()

    @Composable
    override fun DisplayComposable(value: Boolean) {
        BooleanDisplay(key = this, value = value)
    }

    @Composable
    override fun EntryComposable(state: MutableState<Boolean>) {
        BooleanEntry(key = this, state = state)
    }
}

val AccountKeys.mockBool: AccountKey<Boolean>
    get() = MockBoolKey
