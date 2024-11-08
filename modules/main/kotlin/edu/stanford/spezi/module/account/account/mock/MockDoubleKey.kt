package edu.stanford.spezi.module.account.account.mock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.UUID
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.views.display.NumberDisplay
import edu.stanford.spezi.module.account.account.views.entry.NumberEntry
import java.text.NumberFormat

object MockDoubleKey : AccountKey<Double> {
    override val uuid = UUID()
    override val name = StringResource("Double Key")
    override val identifier = "mockDouble"
    override val category = AccountKeyCategory.other
    override val initialValue = InitialValue.Default(0.0)

    @Composable
    override fun DisplayComposable(value: Double) {
        NumberDisplay(key = this, value = value)
    }

    @Composable
    override fun EntryComposable(state: MutableState<Double>) {
        NumberEntry(key = this, state = state, format = NumberFormat.getInstance(), convert = { it.toDouble() })
    }
}

val AccountKeys.mockDouble: AccountKey<Double>
    get() = MockDoubleKey
