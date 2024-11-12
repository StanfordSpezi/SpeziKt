package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.module.account.account.value.AccountKey

@Composable
fun StringEntry(
    key: AccountKey<String>,
    state: MutableState<String>,
) {
    ListRow(key.name) {
        VerifiableTextField(
            key.name,
            text = state,
            disableAutocorrection = true
        )
    }
}
