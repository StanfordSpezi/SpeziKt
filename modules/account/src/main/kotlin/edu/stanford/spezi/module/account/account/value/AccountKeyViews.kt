package edu.stanford.spezi.module.account.account.value

import androidx.compose.runtime.Composable
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.entry.GeneralizedEntry
import edu.stanford.spezi.module.account.account.views.overview.AccountOverviewFormViewModel
import edu.stanford.spezi.module.account.account.views.overview.SingleEntry

@Composable
internal fun <Value : Any> AccountKey<Value>.EntryWithEmptyValue() {
    GeneralizedEntry(this, initialValue = initialValue.value)
}

@Composable
internal fun <Value : Any> AccountKey<Value>.EntryWithStoredOrInitialValue(details: AccountDetails) {
    val value = details[this] ?: initialValue.value
    GeneralizedEntry(this, initialValue = value)
}

@Composable
internal fun <Value : Any> AccountKey<Value>.SingleEntry(
    model: AccountOverviewFormViewModel,
    details: AccountDetails,
) {
    SingleEntry(this, model = model, details = details)
}
