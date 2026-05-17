package edu.stanford.spezi.account.internal.screen

import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.ui.StringResource

/**
 * Sealed input type describing which account sheet to build.
 *
 * Pass one of the variants to [AccountSheetController.getSheet] to receive the corresponding
 * [AccountSheet]. Each variant bundles its own sheet-specific arguments and result callback,
 * keeping the controller's public API to a single method.
 *
 * All variants share the same result type [AccountSheetEvent].
 */
internal sealed interface AccountSheetRequest {

    /**
     * Request to open the single-field edit sheet for one [edu.stanford.spezi.account.AccountKey].
     *
     * @param key The account key whose value should be edited.
     * @param currentValue The pre-filled current value shown in the entry widget.
     * @param onEvent Callback invoked when the sheet emits a result.
     */
    data class EditKey(
        val key: AnyAccountKey,
        val currentValue: Any,
        val onEvent: (AccountSheetEvent) -> Unit,
    ) : AccountSheetRequest

    /**
     * Request to open the "Name & User id" overview sheet.
     *
     * @param onEvent Callback invoked when the sheet emits a result.
     */
    data class NameOverview(
        val title: StringResource,
        val onEvent: (AccountSheetEvent) -> Unit,
    ) : AccountSheetRequest

    /**
     * Request to open the "Sign-In & Security" overview sheet.
     *
     * @param onEvent Callback invoked when the sheet emits a result.
     */
    data class SecurityOverview(
        val onEvent: (AccountSheetEvent) -> Unit,
    ) : AccountSheetRequest

    /**
     * Request to open the two-field "Change Password" sheet.
     *
     * @param onEvent Callback invoked when the sheet emits a result.
     */
    data class ChangePassword(
        val onEvent: (AccountSheetEvent) -> Unit,
    ) : AccountSheetRequest

    /**
     * Request to open the "Sign Up" sheet.
     *
     * @param onEvent Callback invoked when the sheet emits a result.
     */
    data class SignUp(
        val onEvent: (AccountSheetEvent) -> Unit,
    ) : AccountSheetRequest
}

internal sealed interface AccountSheetEvent {
    data object Dismissed : AccountSheetEvent
    data object Success : AccountSheetEvent
}
