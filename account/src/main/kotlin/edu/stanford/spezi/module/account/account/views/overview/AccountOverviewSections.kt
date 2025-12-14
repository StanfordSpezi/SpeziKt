package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.AccountDeletionBehavior
import edu.stanford.spezi.module.account.account.AccountOverviewCloseBehavior
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

@Composable
internal fun AccountOverviewSections(
    account: Account,
    details: AccountDetails,
    closeBehavior: AccountOverviewCloseBehavior,
    deletionBehavior: AccountDeletionBehavior,
    additionalSections: LazyListScope.() -> Unit,
) {
    val isEditing = remember { mutableStateOf(false) }

    val showDeleteButton = when (deletionBehavior) {
        AccountDeletionBehavior.DISABLED -> false
        AccountDeletionBehavior.EDIT_MODE -> isEditing.value
        AccountDeletionBehavior.BELOW_LOGOUT -> true
    }

    val showLogoutButton =
        if (deletionBehavior == AccountDeletionBehavior.EDIT_MODE) {
            !isEditing.value
        } else { true }

    val validation = remember { mutableStateOf(ValidationContext()) }
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val destructiveViewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val viewModel = hiltViewModel<AccountOverviewFormViewModel>()

    ViewStateAlert(viewState)
    ViewStateAlert(destructiveViewState)

    Column {
        AccountOverviewHeader(details, modifier = Modifier.fillMaxWidth())
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultSections(details, viewModel)
            sections(details, viewModel, isEditing)
            if (showLogoutButton) {
                item {
                    SuspendButton("Delete Account", state = destructiveViewState) {
                        TODO("viewModel.presentingRemovalAlert = true")
                    }
                }
            }
            if (showDeleteButton) {
                item {
                    SuspendButton("Logout", state = destructiveViewState) {
                        TODO("model.presentingLogoutAlert = true")
                    }
                }
            }
            additionalSections()
        }
    }
}

private fun LazyListScope.defaultSections(
    details: AccountDetails,
    viewModel: AccountOverviewFormViewModel,
) {
    val displayName = viewModel.displaysNameDetails(details)
    val displaySecurity = viewModel.displaysSignInSecurityDetails(details)

    if (displayName || displaySecurity) {
        item {
            val account = LocalAccount.current ?: error("No account injected")

            if (displayName) {
                @OptIn(ExperimentalMaterial3Api::class)
                Label(
                    label = {
                        DetailsSectionIcon()
                    },
                    content = {
                        Text(
                            viewModel
                                .accountIdentifierLabel(
                                    account.configuration,
                                    details,
                                )
                                .map { it.text() }
                                .joinToString(""),
                        )
                    },
                    modifier = Modifier.clickable {
                        TODO("Navigate to NameOverview(viewModel, details)")
                    },
                )
            }
            if (displaySecurity) {
                @OptIn(ExperimentalMaterial3Api::class)
                Label(
                    label = {
                        SecuritySectionIcon()
                    },
                    content = {
                        Text(
                            "Sign-In & Security"
                        )
                    },
                    modifier = Modifier.clickable {
                        TODO("Navigate to SecurityOverview(viewModel, details)")
                    },
                )
            }
        }
    }
}

private fun LazyListScope.sections(
    details: AccountDetails,
    viewModel: AccountOverviewFormViewModel,
    isEditing: MutableState<Boolean>,
) {
    val keys = viewModel.editableAccountKeys(details)

    for (section in keys.entries) {
        val isNotEmpty = isEditing.value || section.value.any { details.contains(it) }

        if (isNotEmpty) {
            item {
                Column {
                    section.key.categoryTitle?.let {
                        Text(it.text(), style = TextStyles.headlineSmall)
                    }

                    for (key in section.value) {
                        AccountKeyOverviewRow(key, details, isEditing, viewModel)
                    }
                }
            }
        }
    }
}
