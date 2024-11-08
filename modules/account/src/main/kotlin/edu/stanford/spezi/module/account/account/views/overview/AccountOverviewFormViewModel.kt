package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.Account
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.requiredAccountKeys
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.isHiddenCredential
import edu.stanford.spezi.module.account.account.value.keys.accountServiceConfiguration
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.value.keys.userIdType
import edu.stanford.spezi.module.account.account.value.value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.EnumSet
import javax.inject.Inject

@HiltViewModel
internal class AccountOverviewFormViewModel @Inject constructor(
    valueConfiguration: AccountValueConfiguration,
    serviceConfiguration: AccountServiceConfiguration
) : ViewModel() {
    private val logger by speziLogger()

    private val categorizedAccountKeys = valueConfiguration.allCategorized(
        filters = EnumSet.of(
            AccountKeyRequirement.REQUIRED,
            AccountKeyRequirement.COLLECTED,
            AccountKeyRequirement.SUPPORTED
        )
    )
    private val accountServiceConfiguration = serviceConfiguration

    data class UiState(
        val presentingCancellationDialog: Boolean = false,
        val presentingLogoutAlert: Boolean = false,
        val presentingRemovalAlert: Boolean = false,
        val addedAccountKeys: MutableMap<AccountKeyCategory, MutableList<AccountKey<*>>> = mutableMapOf(),
        val modifiedDetails: AccountDetails = AccountDetails(),
        val removedAccountKeys: MutableMap<AccountKeyCategory, MutableList<AccountKey<*>>> = mutableMapOf()
    ) {
        val hasUnsavedChanges: Boolean
            get() = !modifiedDetails.isEmpty()
    }

    companion object {
        operator fun invoke(account: Account, details: AccountDetails) =
            AccountOverviewFormViewModel(account.configuration, details.accountServiceConfiguration)
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val addedAccountKeys: MutableMap<AccountKeyCategory, MutableList<AccountKey<*>>>
        get() = uiState.value.addedAccountKeys

    val modifiedDetails: AccountDetails
        get() = uiState.value.modifiedDetails

    val removedAccountKeys: MutableMap<AccountKeyCategory, MutableList<AccountKey<*>>>
        get() = uiState.value.removedAccountKeys

    val hasUnsavedChanges: Boolean
        get() = !modifiedDetails.isEmpty()

    val defaultErrorDescription: StringResource
        get() = StringResource("ACCOUNT_OVERVIEW_EDIT_DEFAULT_ERROR")


    fun accountKeys(category: AccountKeyCategory, details: AccountDetails): List<AccountKey<*>> {
        val result = (categorizedAccountKeys[category] ?: emptyList())
            .sortedWith(
                AccountOverviewValuesComparator(
                    details,
                    addedAccountKeys,
                    removedAccountKeys
                )
            )
            .toMutableList()

        for (describedKey in accountServiceConfiguration.requiredAccountKeys) {
            if (describedKey.category == category && !result.contains(describedKey)) {
                result.add(describedKey)
            }
        }

        return result
    }

    private fun baseSortedAccountKeys(details: AccountDetails): Map<AccountKeyCategory, List<AccountKey<*>>> {
        val results = categorizedAccountKeys
            .mapValues { it.value.toMutableList() }
            .toMutableMap()


        for (describedKey in accountServiceConfiguration.requiredAccountKeys) {
            results[describedKey.category]?.add(describedKey) ?: run {
                results[describedKey.category] = mutableListOf(describedKey)
            }
        }

        return results.mapValues {
            it.value.sortedWith(
                AccountOverviewValuesComparator(
                    details,
                    addedAccountKeys,
                    removedAccountKeys
                )
            )
        }
    }

    fun editableAccountKeys(details: AccountDetails): Map<AccountKeyCategory, List<AccountKey<*>>> {
        return baseSortedAccountKeys(details).filter {
            it.key != AccountKeyCategory.credentials && it.key != AccountKeyCategory.name
        }
    }

    fun namesOverviewKeys(details: AccountDetails): List<AccountKey<*>> {
        val result = baseSortedAccountKeys(details).filter {
            it.key == AccountKeyCategory.credentials || it.key == AccountKeyCategory.name
        }.toMutableMap()

        if (details.isAnonymous) {
            result.remove(AccountKeyCategory.credentials)
        } else if (result[AccountKeyCategory.credentials]?.any { it.isEqualTo(AccountKeys.userId) } == true) {
            result[AccountKeyCategory.credentials] = listOf(AccountKeys.userId)
        }

        return result.asIterable().fold(emptyList()) { acc, entry ->
            acc + entry.value
        }
    }

    fun addAccountDetail(key: AccountKey<*>) {
        if (addedAccountKeys[key.category].orEmpty().contains(key)) {
            return
        }

        logger.w { "Adding new account value $key to the edit view!" }

        val index = removedAccountKeys[key.category].orEmpty().indexOf(key)

        if (index >= 0) {
            removedAccountKeys[key.category]?.removeAt(index)
        } else {
            addedAccountKeys[key.category]?.add(key) ?: run {
                addedAccountKeys[key.category] = mutableListOf(key)
            }
        }
    }

    fun deleteAccountKeys(indexSet: Set<Int>, accountKeys: List<AccountKey<*>>) {
        for (keyIndex in indexSet) {
            val value = accountKeys[keyIndex]

            val index = addedAccountKeys[value.category].orEmpty().indexOf(value)
            if (index >= 0) {
                addedAccountKeys[value.category]?.removeAt(index)

                modifiedDetails.remove(value)
            } else {
                removedAccountKeys[value.category]?.add(value) ?: run {
                    removedAccountKeys[value.category] = mutableListOf(value)
                }

                value.setEmpty(modifiedDetails)
            }
        }
    }

    fun cancelEditAction(isEditing: MutableState<Boolean>?) {
        logger.w { "Pressed the cancel button!" }

        if (!hasUnsavedChanges) {
            discardChangesAction(isEditing)
            return
        }

        _uiState.update { it.copy(presentingCancellationDialog = true) }

        logger.w { "Found ${modifiedDetails.keys.count()} modified elements. Asking to discard." }
    }

    fun discardChangesAction(isEditing: MutableState<Boolean>?) {
        discardChangesAction()

        isEditing?.value = false
    }

    fun discardChangesAction() {
        logger.w { "Exiting edit mode and discarding changes." }

        resetModelState()
    }

    suspend fun updateAccountDetails(
        details: AccountDetails,
        account: Account,
        isEditing: MutableState<Boolean>? = null
    ) {
        val removedAccountKeys =
            removedAccountKeys.values.fold(emptyList<AccountKey<*>>()) { acc, keys -> acc + keys }
        val removedDetails = AccountDetails()
        removedDetails.addContentsOf(details, removedAccountKeys)

        val modifications = AccountModifications(
            modifiedDetails,
            removedDetails,
        )

        account.accountService.updateAccountDetails(modifications)
        logger.w { "${details.keys.count()} items saved successfully." }

        resetModelState()
        isEditing?.value = false
    }

    fun resetModelState(isEditing: MutableState<Boolean>? = null) {
        _uiState.update {
            it.copy(
                addedAccountKeys = mutableMapOf(),
                removedAccountKeys = mutableMapOf(),
                modifiedDetails = AccountDetails()
            )
        }

        isEditing?.value = false
    }

    fun accountIdentifierLabel(configuration: AccountValueConfiguration, details: AccountDetails): List<StringResource> {
        val userId = details.userIdType.stringResource

        if (configuration[AccountKeys.name] != null) {
            if (details.isAnonymous) {
                return listOf(AccountKeys.name.name)
            }

            return listOf(
                AccountKeys.name.name,
                StringResource(", "),
                userId
            )
        }

        return listOf(userId)
    }

    fun displaysSignInSecurityDetails(details: AccountDetails): Boolean {
        return !details.isAnonymous &&
            accountKeys(AccountKeyCategory.credentials, details).any { !it.isHiddenCredential }
    }

    fun displayNameDetails(details: AccountDetails): Boolean {
        val containsUserId = categorizedAccountKeys[AccountKeyCategory.credentials]?.contains(AccountKeys.userId)
        return (containsUserId == true && !details.isAnonymous) ||
            (categorizedAccountKeys[AccountKeyCategory.name]?.isEmpty() != true)
    }
}

private fun <Value : Any> AccountKey<Value>.setEmpty(details: AccountDetails) {
    details[this] = this.initialValue.value
}



