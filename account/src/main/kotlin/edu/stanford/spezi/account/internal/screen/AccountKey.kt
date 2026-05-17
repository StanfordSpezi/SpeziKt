package edu.stanford.spezi.account.internal.screen

import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.userIdConfiguration
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.DataEntryComposable
import edu.stanford.spezi.ui.account.StringDataEntry

/**
 * Retrieves the display name for the given [key] by accounting also for the user id [AccountService] configuration.
 */
internal fun AccountService.displayName(key: AnyAccountKey): StringResource {
    return if (key == AccountKeys.userId) configuration.userIdConfiguration.idType.label else key.name
}

/**
 * Retrieves the entry composable for the given [key] by accounting also for the user id [AccountService] configuration.
 */
@Suppress("UNCHECKED_CAST")
internal fun <V : Any> AccountService.requireEntryComposable(key: AccountKey<V>): DataEntryComposable<V> {
    val entry = requireNotNull(key.entry) { "No entry composable defined for key '${key.name}'" }
    return if (key == AccountKeys.userId && entry is StringDataEntry) {
        entry.copy(placeholder = configuration.userIdConfiguration.idType.label) as DataEntryComposable<V>
    } else {
        entry
    }
}
