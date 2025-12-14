package edu.stanford.spezi.module.account.account.views.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity

interface StringResourceConvertible {
    val stringResource: StringResource
}

@Composable
fun <Value> StringResourceDisplay(
    key: AccountKey<Value>,
    value: Value,
) where Value : StringResourceConvertible, Value : Any {
    ListRow(key.name.text()) {
        Text(value.stringResource.text())
    }
}

@ThemePreviews
@Composable
private fun StringResourceDisplayPreview() {
    SpeziTheme(isPreview = true) {
        AccountKeys.genderIdentity
            .Display(GenderIdentity.PREFER_NOT_TO_STATE)
    }
}
