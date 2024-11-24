package edu.stanford.spezi.module.account.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.account.account.compositionLocal.LocalFollowUpBehavior
import edu.stanford.spezi.module.account.account.compositionLocal.ReceiveSignupProviderCompliance
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.views.documentation.EmptyServicesWarning
import edu.stanford.spezi.module.account.account.views.setup.DefaultAccountSetupHeader
import edu.stanford.spezi.module.account.account.views.setup.ExistingAccountComposable
import edu.stanford.spezi.module.account.account.views.setup.FollowUpInfoSheet
import edu.stanford.spezi.module.account.account.views.setup.ServicesDivider

sealed interface AccountSetupState {
    data object Generic : AccountSetupState
    data object SetupShown : AccountSetupState
    data class RequiringAdditionalInfo(val keys: List<AccountKey<*>>) : AccountSetupState
    data object LoadingExistingAccount : AccountSetupState

    companion object {
        val DefaultValue = Generic
    }
}

val LocalAccountSetupState = compositionLocalOf<AccountSetupState> { AccountSetupState.Generic }

@Composable
fun AccountSetup(
    setupComplete: suspend (AccountDetails) -> Unit = {},
    header: @Composable () -> Unit,
    done: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<AccountSetupViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val followUpBehavior = LocalFollowUpBehavior.current
    LaunchedEffect(followUpBehavior) {
        viewModel.onAction(AccountSetupViewModel.Action.UpdateFollowUpBehavior(followUpBehavior))
    }

    LazyColumn {
        item {
            Box(Modifier.fillMaxWidth()) {
                AccountSetupScrollableContent(
                    viewModel,
                    uiState,
                    header,
                    setupComplete
                )
            }
        }
    }
}

@Composable
private fun AccountSetupScrollableContent(
    viewModel: AccountSetupViewModel,
    uiState: AccountSetupViewModel.UiState,
    header: @Composable () -> Unit,
    setupComplete: suspend (AccountDetails) -> Unit,
) {
    Column {
        if (viewModel.categorizedIdentityProviders.isNotEmpty()) {
            CompositionLocalProvider(LocalAccountSetupState provides uiState.setupState) {
                header()
            }
        }

        val accountDetails = viewModel.accountDetails
        if (accountDetails?.isAnonymous == false) {
            when (uiState.setupState) {
                is AccountSetupState.RequiringAdditionalInfo -> {
                    if (uiState.displaysFollowUpSheet) {
                        FollowUpInfoSheet(
                            uiState.setupState.keys,
                            onComplete = {
                                viewModel.onAction(
                                    AccountSetupViewModel.Action.FollowUpSheetCompleted(
                                        it, setupComplete
                                    )
                                )
                            },
                        )
                    }
                }
                is AccountSetupState.LoadingExistingAccount -> {
                    CircularProgressIndicator()
                }
                else -> {
                    if (uiState.isCompletingSetup) {
                        CircularProgressIndicator()
                    } else {
                        ExistingAccountComposable(
                            accountDetails
                        )
                    }
                }
            }
        } else {
            AccountSetupComponentsContent(viewModel)
        }
    }
}

@Composable
private fun AccountSetupComponentsContent(
    viewModel: AccountSetupViewModel,
) {
    if (viewModel.categorizedIdentityProviders.isEmpty()) {
        EmptyServicesWarning()
    } else {
        ReceiveSignupProviderCompliance(action = {
            viewModel.onAction(AccountSetupViewModel.Action.ReceiveSignupProviderCompliance(it))
        }) {
            Column(Modifier.padding(horizontal = Spacings.medium)) {
                val lastKey = viewModel.categorizedIdentityProviders.lastOrNull()?.first
                for (entry in viewModel.categorizedIdentityProviders) {
                    entry.second.content()

                    if (entry.first != lastKey) {
                        ServicesDivider()
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AccountSetupPreview() {
    AccountSetup(
        setupComplete = {
            println("setupComplete")
        },
        header = {
            DefaultAccountSetupHeader()
        },
        done = {}
    )
}
