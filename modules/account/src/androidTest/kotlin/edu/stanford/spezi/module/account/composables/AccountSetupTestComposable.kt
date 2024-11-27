package edu.stanford.spezi.module.account.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.module.account.account.mock.MockBoolKey
import edu.stanford.spezi.module.account.account.mock.MockDoubleKey
import edu.stanford.spezi.module.account.account.mock.MockNumericKey
import edu.stanford.spezi.module.account.account.views.entry.BooleanEntry
import edu.stanford.spezi.module.account.account.views.entry.NumberEntry
import java.text.NumberFormat

@Composable
fun AccountSetupTestComposable() {
    val toggle = remember { mutableStateOf(false) }
    val integer = remember { mutableLongStateOf(0) }
    val double = remember { mutableDoubleStateOf(1.5) }

    Column(Modifier.fillMaxWidth().padding(32.dp)) {
        BooleanEntry(
            MockBoolKey,
            modifier = Modifier.testIdentifier(AccountSetupTestIdentifier.BOOLEAN),
            value = toggle.value,
            onValueChanged = {
                toggle.value = it
            },
        )

        NumberEntry(
            MockNumericKey,
            modifier = Modifier.testIdentifier(AccountSetupTestIdentifier.LONG),
            value = integer.longValue,
            format = NumberFormat.getIntegerInstance(),
            convert = { it.toLong() },
            onValueChanged = {
                integer.longValue = it
            },
        )

        NumberEntry(
            MockDoubleKey,
            modifier = Modifier.testIdentifier(AccountSetupTestIdentifier.DOUBLE),
            value = double.doubleValue,
            format = NumberFormat.getNumberInstance(),
            convert = { it.toDouble() },
            onValueChanged = {
                double.doubleValue = it
            },
        )
    }
}

enum class AccountSetupTestIdentifier {
    BOOLEAN, LONG, DOUBLE
}

@ThemePreviews
@Composable
private fun AccountSetupTestComposablePreview() {
    SpeziTheme(isPreview = true) {
        AccountSetupTestComposable()
    }
}
