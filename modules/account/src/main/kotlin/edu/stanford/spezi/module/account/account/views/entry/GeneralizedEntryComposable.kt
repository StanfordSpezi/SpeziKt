package edu.stanford.spezi.module.account.account.views.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.modifier.ValidateRequired
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement

@Composable
fun <Value : Any> GeneralizedEntryComposable(
    key: AccountKey<Value>,
    initialValue: Value,
) {
    val value = remember { mutableStateOf(initialValue) }
    val account = LocalAccount.current

    (value.value as? String)?.let { stringValue ->
        // TODO: Figure out how to get validation rules!
        Validate(stringValue, emptyList()) {
            key.EntryComposable(value)
        }
    } ?: run {
        val isRequiredNonEmpty = (
            key.initialValue is InitialValue.Empty &&
                account?.configuration?.get(key)?.requirement == AccountKeyRequirement.REQUIRED
            )

        if (isRequiredNonEmpty) {
            ValidateRequired(key, value) {
                key.EntryComposable(value)
            }
        } else {
            key.EntryComposable(value)
        }
    }
}
