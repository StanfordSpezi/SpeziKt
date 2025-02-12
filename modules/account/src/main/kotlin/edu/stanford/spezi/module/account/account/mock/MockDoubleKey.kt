package edu.stanford.spezi.module.account.account.mock

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.views.display.NumberDisplay
import edu.stanford.spezi.module.account.account.views.entry.NumberEntry
import kotlinx.serialization.builtins.serializer
import java.text.NumberFormat

object MockDoubleKey : AccountKey<Double> {
    override val name = StringResource("Double Key")
    override val identifier = "mockDouble"
    override val category = AccountKeyCategory.other
    override val initialValue = InitialValue.Default(0.0)
    override val serializer = Double.serializer()

    @Composable
    override fun Display(value: Double) {
        NumberDisplay(key = this, value = value)
    }

    @Composable
    override fun Entry(value: Double, onValueChanged: (Double) -> Unit) {
        NumberEntry(
            key = this,
            value = value,
            onValueChanged = onValueChanged,
            format = NumberFormat.getInstance(),
            convert = { it.toDouble() }
        )
    }
}

val AccountKeys.mockDouble: AccountKey<Double>
    get() = MockDoubleKey
