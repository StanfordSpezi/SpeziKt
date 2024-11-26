package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.EntryWithStoredOrInitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

@Composable
internal fun <Value : Any> SingleEntry(
    key: AccountKey<Value>,
    model: AccountOverviewFormViewModel,
    details: AccountDetails,
) {
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val validation = remember { mutableStateOf(ValidationContext()) }
    val account = LocalAccount.current

    val disabledDone = !model.hasUnsavedChanges ||
        details[key] == model.modifiedDetails[key] ||
        !validation.value.allInputValid

    DisposableEffect(Unit) {
        onDispose {
            model.resetModelState()
        }
    }

    ReceiveValidation(validation) {
        // TODO: ViewStateAlert
        Column {
            key.EntryWithStoredOrInitialValue(details)
            // TODO: .focused($isFocused)
            // TODO: .environment(\.accountViewType, .overview(mode: .existing))
            // TODO: .injectEnvironmentObjects(configuration: accountDetails.accountServiceConfiguration, model: model)
        }

        // TODO:
    }

    suspend fun submitChange() {
        if (!validation.value.validateHierarchy() || account == null) return

        account.logger.w { "Saving updated $key value!" }

        model.updateAccountDetails(details, account)
        // TODO: Dismiss
    }
}
