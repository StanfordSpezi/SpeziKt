package edu.stanford.spezi.module.account.account.views.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey

enum class BooleanDisplayLabel {
    ON_OFF, YES_NO;

    internal val trueLabel: StringResource get() = when (this) {
        ON_OFF -> StringResource("On")
        YES_NO -> StringResource("YES")
    }

    internal val falseLabel: StringResource get() = when (this) {
        ON_OFF -> StringResource("Off")
        YES_NO -> StringResource("NO")
    }
}

@Composable
fun BooleanDisplay(
    label: BooleanDisplayLabel = BooleanDisplayLabel.ON_OFF,
    key: AccountKey<Boolean>,
    value: Boolean,
) {
    ListRow(key.name.text()) {
        val stringResource = if (value) label.trueLabel else label.falseLabel
        Text(stringResource.text())
    }
}
