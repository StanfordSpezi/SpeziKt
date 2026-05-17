package edu.stanford.spezi.account.internal.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeyCategory
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountModifications
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.PasswordKey
import edu.stanford.spezi.account.fieldValidationRules
import edu.stanford.spezi.account.isAnonymousUser
import edu.stanford.spezi.account.isHiddenCredential
import edu.stanford.spezi.core.coroutines.CoroutinesLauncher
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.MutableSpeziScaffoldState
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.AccountChangePasswordLayout
import edu.stanford.spezi.ui.account.AccountOverviewItem
import edu.stanford.spezi.ui.account.AccountOverviewSection
import edu.stanford.spezi.ui.account.AccountSectionOverviewLayout
import edu.stanford.spezi.ui.account.AnyAccountOverviewItem
import edu.stanford.spezi.ui.account.StringDataDisplay
import edu.stanford.spezi.ui.showErrorToast
import edu.stanford.spezi.ui.speziAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Single entry-point for all account-related bottom sheets.
 *
 * Accepts an [AccountSheetRequest] sealed type and returns the matching [AccountSheet].
 */
internal class AccountSheetController(
    private val account: Account,
    private val accountService: AccountService,
    private val signUpSheetController: AccountSignUpSheetController,
    private val editKeySheetController: AccountKeyEditSheetController,
) {
    private val config = accountService.configuration
    private val changePasswordState = MutableStateFlow(ChangePasswordFormState())

    /**
     * Returns a fully configured [AccountSheet] for the given [request].
     *
     * @param request Describes which sheet to build and carries its result callback.
     * @param coroutinesLauncher Launcher used for scaffold coroutines and async submission.
     */
    fun getSheet(
        request: AccountSheetRequest,
        coroutinesLauncher: CoroutinesLauncher,
    ): AccountSheet = when (request) {
        is AccountSheetRequest.SignUp -> signUpSheetController.buildSheet(request, coroutinesLauncher)
        is AccountSheetRequest.EditKey -> editKeySheetController.buildSheet(request, coroutinesLauncher)
        is AccountSheetRequest.NameOverview -> buildNameOverviewSheet(request, coroutinesLauncher)
        is AccountSheetRequest.SecurityOverview -> buildSecurityOverviewSheet(request, coroutinesLauncher)
        is AccountSheetRequest.ChangePassword -> buildChangePasswordSheet(request, coroutinesLauncher)
    }

    private fun buildNameOverviewSheet(
        request: AccountSheetRequest.NameOverview,
        coroutinesLauncher: CoroutinesLauncher,
    ): AccountSheet {
        val scaffoldState = MutableSpeziScaffoldState(
            coroutinesLauncher = coroutinesLauncher,
            appBar = speziAppBar {
                title(request.title)
                close { request.onEvent(AccountSheetEvent.Dismissed) }
            },
        )

        val layout = MutableStateFlow(
            AccountSectionOverviewLayout(
                section = AccountOverviewSection(
                    title = null,
                    items = buildNameItems(scaffoldState, coroutinesLauncher),
                ),
                icon = ImageResource(Icons.Default.AccountCircle),
            )
        )

        return AccountSheet(
            scaffoldState = scaffoldState,
            layout = layout,
            onDismiss = { request.onEvent(AccountSheetEvent.Dismissed) },
        )
    }

    private fun buildNameItems(
        scaffoldState: MutableSpeziScaffoldState,
        coroutinesLauncher: CoroutinesLauncher,
    ): List<AnyAccountOverviewItem> = buildList {
        // All .name category keys with an entry composable.
        account.configuration
            .all()
            .forEach { key ->
                if (key.category == AccountKeyCategory.Name && key.entry != null) {
                    add(buildNameKeyRow(key, scaffoldState, coroutinesLauncher))
                }
            }

        // userId is the only credential shown in the name overview
        val details = account.details.value
        if (details != null && !details.isAnonymousUser) {
            val userIdEntry = account.configuration[AccountKeys.userId::class]
            if (userIdEntry != null) {
                add(buildNameKeyRow(AccountKeys.userId, scaffoldState, coroutinesLauncher))
            }
        }
    }

    private fun buildNameKeyRow(
        key: AnyAccountKey,
        scaffoldState: MutableSpeziScaffoldState,
        coroutinesLauncher: CoroutinesLauncher,
    ) = AccountOverviewItem(
        title = accountService.displayName(key),
        valueDisplay = StringDataDisplay(),
        value = "",
        leadingImage = null,
        showArrow = true,
        onClick = {
            val nested = getSheet(
                request = AccountSheetRequest.EditKey(
                    key = key,
                    currentValue = account.details.value?.getAnyOrNull(key::class) ?: key.initialValue.value,
                    onEvent = { scaffoldState.dismissBottomSheet() },
                ),
                coroutinesLauncher = coroutinesLauncher,
            )
            scaffoldState.showBottomSheet(nested)
        },
    )

    private fun buildSecurityOverviewSheet(
        request: AccountSheetRequest.SecurityOverview,
        coroutinesLauncher: CoroutinesLauncher,
    ): AccountSheet {
        val scaffoldState = MutableSpeziScaffoldState(
            coroutinesLauncher = coroutinesLauncher,
            appBar = speziAppBar {
                title(Strings.account_overview_sign_in_security)
                close { request.onEvent(AccountSheetEvent.Dismissed) }
            },
        )

        val layout = MutableStateFlow(
            AccountSectionOverviewLayout(
                section = AccountOverviewSection(
                    title = null,
                    items = buildSecurityItems(scaffoldState, coroutinesLauncher)
                ),
                icon = ImageResource(Icons.Default.Lock),
            )
        )

        return AccountSheet(
            scaffoldState = scaffoldState,
            layout = layout,
            onDismiss = { request.onEvent(AccountSheetEvent.Dismissed) },
        )
    }

    private fun buildSecurityItems(
        scaffoldState: MutableSpeziScaffoldState,
        coroutinesLauncher: CoroutinesLauncher,
    ): List<AnyAccountOverviewItem> = buildList {
        if (account.configuration[AccountKeys.password::class] != null) {
            add(buildChangePasswordRow(scaffoldState, coroutinesLauncher))
        }
        account.configuration
            .all()
            .filter { key ->
                key !== AccountKeys.password &&
                    key.category == AccountKeyCategory.Credentials &&
                    key.entry != null &&
                    !key.isHiddenCredential
            }
            .forEach { key ->
                @Suppress("UNCHECKED_CAST")
                add(buildCredentialRow(key as AccountKey<Any>, scaffoldState, coroutinesLauncher))
            }
    }

    private fun buildChangePasswordRow(
        scaffoldState: MutableSpeziScaffoldState,
        coroutinesLauncher: CoroutinesLauncher,
    ) = AccountOverviewItem(
        title = StringResource(Strings.account_overview_change_password),
        valueDisplay = StringDataDisplay(),
        value = "",
        leadingImage = null,
        showArrow = true,
        onClick = {
            val nested = getSheet(
                request = AccountSheetRequest.ChangePassword(
                    onEvent = { scaffoldState.dismissBottomSheet() },
                ),
                coroutinesLauncher = coroutinesLauncher,
            )
            scaffoldState.showBottomSheet(nested)
        },
    )

    private fun buildCredentialRow(
        key: AccountKey<Any>,
        scaffoldState: MutableSpeziScaffoldState,
        coroutinesLauncher: CoroutinesLauncher,
    ) = AccountOverviewItem(
        title = key.name,
        valueDisplay = StringDataDisplay(),
        value = "",
        leadingImage = null,
        showArrow = true,
        onClick = {
            val nested = editKeySheetController.buildSheet(
                request = AccountSheetRequest.EditKey(
                    key = key,
                    currentValue = key.initialValue.value,
                    onEvent = { scaffoldState.dismissBottomSheet() },
                ),
                coroutinesLauncher = coroutinesLauncher,
            )
            scaffoldState.showBottomSheet(nested)
        },
    )

    private fun buildChangePasswordSheet(
        request: AccountSheetRequest.ChangePassword,
        coroutinesLauncher: CoroutinesLauncher,
    ): AccountSheet {
        changePasswordState.update { ChangePasswordFormState() }

        val scaffoldState = MutableSpeziScaffoldState(
            coroutinesLauncher = coroutinesLauncher,
            appBar = speziAppBar {
                title(Strings.account_overview_change_password)
                close { request.onEvent(AccountSheetEvent.Dismissed) }
            },
        )

        return AccountSheet(
            scaffoldState = scaffoldState,
            layout = changePasswordState.map { state ->
                buildChangePasswordLayout(state, scaffoldState, request.onEvent)
            },
            onDismiss = { request.onEvent(AccountSheetEvent.Dismissed) },
        )
    }

    private fun buildChangePasswordLayout(
        state: ChangePasswordFormState,
        scaffoldState: MutableSpeziScaffoldState,
        onEvent: (AccountSheetEvent) -> Unit,
    ) = AccountChangePasswordLayout(
        newPassword = state.newPassword,
        confirmPassword = state.confirmPassword,
        onNewPasswordChange = { value ->
            changePasswordState.update { it.copy(newPassword = value, confirmError = null) }
        },
        onConfirmPasswordChange = { value ->
            changePasswordState.update { it.copy(confirmPassword = value, confirmError = null) }
        },
        validationMessage = state.confirmError,
        saveButton = AsyncTextButton(
            title = StringResource(Strings.account_edit_save_button),
            enabled = state.canSave,
            action = { submitChangePassword(scaffoldState, onEvent) },
        ),
        icon = ImageResource(Icons.Default.Lock),
    )

    private suspend fun submitChangePassword(
        scaffoldState: MutableSpeziScaffoldState,
        onEvent: (AccountSheetEvent) -> Unit,
    ) {
        val state = changePasswordState.value
        val newPassword = state.newPassword

        val ruleError = config
            .fieldValidationRules(PasswordKey::class)
            ?.firstNotNullOfOrNull { it.validate(newPassword)?.message }

        if (ruleError != null) {
            changePasswordState.update { it.copy(confirmError = ruleError) }
            return
        }

        if (newPassword != state.confirmPassword) {
            changePasswordState.update {
                it.copy(confirmError = StringResource(Strings.account_change_password_no_match))
            }
            return
        }

        val modifiedDetails = AccountDetails()
        modifiedDetails[AccountKeys.password::class] = newPassword

        AccountModifications(modifiedDetails = modifiedDetails)
            .onSuccess { modifications ->
                accountService.updateAccountDetails(modifications)
                    .onSuccess { onEvent(AccountSheetEvent.Success) }
                    .onFailure {
                        scaffoldState.showErrorToast(
                            message = StringResource(Strings.account_change_password_failed),
                        )
                    }
            }
    }

    private data class ChangePasswordFormState(
        val newPassword: String = "",
        val confirmPassword: String = "",
        val confirmError: StringResource? = null,
    ) {
        val canSave: Boolean get() = newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
    }
}
