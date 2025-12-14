package edu.stanford.spezi.module.account.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.module.account.account.compositionLocal.FollowUpBehavior
import edu.stanford.spezi.module.account.account.compositionLocal.SignupProviderCompliance
import edu.stanford.spezi.module.account.account.compositionLocal.SignupProviderCompliance.VisualizedAccountKeys
import edu.stanford.spezi.module.account.account.service.identityProvider.AccountSetupSection
import edu.stanford.spezi.module.account.account.service.identityProvider.IdentityProvider
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AccountSetupViewModel @Inject constructor(
    private val account: Account,
) : ViewModel() {

    data class UiState(
        val setupState: AccountSetupState = AccountSetupState.Generic,
        val compliance: SignupProviderCompliance? = null,
        val displaysFollowUpSheet: Boolean = false,
        val isCompletingSetup: Boolean = false,
        val followUpBehavior: FollowUpBehavior = FollowUpBehavior.automatic,
    )

    sealed interface Action {
        data class ReceiveSignupProviderCompliance(val compliance: SignupProviderCompliance?) : Action
        data class CheckAccountState(val setupComplete: suspend (AccountDetails) -> Unit) : Action
        data class HandleSetupCompleted(val setupComplete: suspend (AccountDetails) -> Unit) : Action
        data class UpdateFollowUpBehavior(val followUpBehavior: FollowUpBehavior) : Action
        data class FollowUpSheetCompleted(
            val modifications: AccountModifications,
            val setupComplete: suspend (AccountDetails) -> Unit,
        ) : Action
    }

    val accountDetails: AccountDetails? get() =
        account.details

    // TODO: In contrast to iOS, this doesn't ensure a section to not be used by multiple providers,
    //  but this would be a misuse of the framework anyways, right?
    val categorizedIdentityProviders: List<Pair<AccountSetupSection, IdentityProvider>> =
        account.accountSetupComponents
            .filter { it.isEnabled }
            .map { Pair(it.configuration.section, it) }
            .sortedBy { it.first.rawValue }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.ReceiveSignupProviderCompliance -> {
                _uiState.update { it.copy(compliance = action.compliance) }
            }
            is Action.CheckAccountState -> {
                viewModelScope.launch {
                    val details = account.details
                    if (details?.isAnonymous == false && uiState.value.setupState == AccountSetupState.SetupShown) {
                        handleSuccessfulSetup(details, action.setupComplete)
                    }
                }
            }
            is Action.HandleSetupCompleted -> {
                viewModelScope.launch {
                    account.details?.let {
                        handleSetupCompleted(it, action.setupComplete)
                    }
                }
            }
            is Action.FollowUpSheetCompleted -> {
                _uiState.update { it.copy(isCompletingSetup = false) }
                viewModelScope.launch {
                    account.details?.let {
                        handleSetupCompleted(it, action.setupComplete)
                    }
                }
            }
            is Action.UpdateFollowUpBehavior -> {
                _uiState.update { it.copy(followUpBehavior = action.followUpBehavior) }
            }
        }
    }

    private suspend fun handleSuccessfulSetup(
        details: AccountDetails,
        setupComplete: suspend (AccountDetails) -> Unit,
    ) {
        val ignoreCollected: List<AccountKey<*>>

        var includeCollected = when (uiState.value.followUpBehavior) {
            FollowUpBehavior.DISABLED -> {
                handleSetupCompleted(details, setupComplete)
                return
            }
            FollowUpBehavior.MINIMAL -> {
                AccountValueConfiguration.IncludeCollectedType
                    .ONLY_REQUIRED
            }
            FollowUpBehavior.REDUNDANT -> {
                AccountValueConfiguration.IncludeCollectedType
                    .INCLUDE_COLLECTED_AT_LEAST_ONE_REQUIRED
            }
        }

        val visualizedAccountKeysValue = uiState.value.compliance?.visualizedAccountKeys
        val visualizedAccountKeys = if (visualizedAccountKeysValue is VisualizedAccountKeys.Only) {
            visualizedAccountKeysValue.keys
        } else {
            null
        }
        if (details.isNewUser && visualizedAccountKeys != null) {
            includeCollected = AccountValueConfiguration.IncludeCollectedType
                .ONLY_REQUIRED
        }
        ignoreCollected = visualizedAccountKeys ?: emptyList()

        val missingKeys = account.configuration
            .missingRequiredKeys(details, includeCollected, ignoreCollected)

        if (missingKeys.isNotEmpty()) {
            _uiState.update { it.copy(setupState = AccountSetupState.RequiringAdditionalInfo(missingKeys)) }
        } else {
            handleSetupCompleted(details, setupComplete)
        }
    }

    private suspend fun handleSetupCompleted(
        details: AccountDetails,
        setupComplete: suspend (AccountDetails) -> Unit,
    ) {
        _uiState.update { it.copy(isCompletingSetup = true) }
        setupComplete(details)
        _uiState.update { it.copy(isCompletingSetup = false) }
    }
}
