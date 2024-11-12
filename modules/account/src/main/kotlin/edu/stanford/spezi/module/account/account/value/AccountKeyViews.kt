package edu.stanford.spezi.module.account.account.value

import androidx.compose.runtime.Composable
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.entry.GeneralizedEntryComposable
import edu.stanford.spezi.module.account.account.views.overview.AccountOverviewFormViewModel
import edu.stanford.spezi.module.account.account.views.overview.SingleEntryComposable

@Composable
internal fun <Value : Any> AccountKey<Value>.EntryComposableWithEmptyValue() {
    GeneralizedEntryComposable(this, initialValue = initialValue.value)
}

@Composable
internal fun <Value : Any> AccountKey<Value>.EntryComposableWithStoredOrInitialValue(details: AccountDetails) {
    val value = details[this] ?: initialValue.value
    GeneralizedEntryComposable(this, initialValue = value)
}

@Composable
internal fun <Value : Any> AccountKey<Value>.SingleEntryComposable(
    model: AccountOverviewFormViewModel,
    details: AccountDetails,
) {
    SingleEntryComposable(this, model = model, details = details)
}
