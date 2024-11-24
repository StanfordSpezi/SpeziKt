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

private object MockNumericKey : AccountKey<Long> {
    override val name = StringResource("Numeric Key")
    override val identifier = "mockNumeric"
    override val category = AccountKeyCategory.other
    override val initialValue = InitialValue.Default(0L)
    override val serializer = Long.serializer()

    @Composable
    override fun DisplayComposable(value: Long) {
        NumberDisplay(key = this, value = value)
    }

    @Composable
    override fun EntryComposable(value: Long, onValueChanged: (Long) -> Unit) {
        NumberEntry(
            key = this,
            value = value,
            onValueChanged = onValueChanged,
            format = NumberFormat.getInstance(),
            convert = { it.toLong() }
        )
    }
}

val AccountKeys.mockNumeric: AccountKey<Long>
    get() = MockNumericKey
