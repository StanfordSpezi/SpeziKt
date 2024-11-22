package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.isHiddenCredential
import edu.stanford.spezi.module.account.account.value.keys.password

@Composable
internal fun SecurityOverview(
    model: AccountOverviewFormViewModel,
    details: AccountDetails,
) {
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val presentingPasswordChangeSheet = remember { mutableStateOf(false) }
    val keys = model.accountKeys(AccountKeyCategory.credentials, details)
        .filter { !it.isHiddenCredential }

    if (presentingPasswordChangeSheet.value) {
        PasswordChangeSheet(model, details) {
            presentingPasswordChangeSheet.value = false
        }
    }

    ViewStateAlert(viewState)

    for (key in keys) {
        if (key == AccountKeys.password) {
            Button(
                onClick = {
                    presentingPasswordChangeSheet.value = true
                }
            ) {
                Text(StringResource("CHANGE_PASSWORD").text())
            }
        } else {
            // AccountKeyOverviewRow(details, key, model)
        }
    }
}
