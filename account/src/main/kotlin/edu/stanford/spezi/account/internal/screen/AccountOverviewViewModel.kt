package edu.stanford.spezi.account.internal.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeyCategory
import edu.stanford.spezi.account.AccountKeyOptions
import edu.stanford.spezi.account.AccountKeyRequirement
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.isAnonymousUser
import edu.stanford.spezi.account.isHiddenCredential
import edu.stanford.spezi.account.userIdType
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.EventSink
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.LoadingLayout
import edu.stanford.spezi.ui.MutableSpeziScaffoldState
import edu.stanford.spezi.ui.SpeziScaffoldState
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.AccountEditButton
import edu.stanford.spezi.ui.account.AccountOverviewItem
import edu.stanford.spezi.ui.account.AccountOverviewLayout
import edu.stanford.spezi.ui.account.AccountOverviewSection
import edu.stanford.spezi.ui.account.AccountProfileHeader
import edu.stanford.spezi.ui.account.StringDataDisplay
import edu.stanford.spezi.ui.coroutinesLauncher
import edu.stanford.spezi.ui.showErrorToast
import edu.stanford.spezi.ui.speziAppBar
import edu.stanford.spezi.ui.theme.Colors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the account overview screen.
 *
 * Builds the overview layout from the currently signed-in user's [AccountDetails], grouping
 * account keys into sections by category. Manages edit mode, triggers bottom sheets for
 * editing individual keys or navigating to name and security overviews, and handles logout.
 * Dismisses the screen automatically when the user signs out.
 */
internal class AccountOverviewViewModel(
    private val account: Account,
    private val accountSheetController: AccountSheetController,
) : ViewModel() {

    private val eventSink = EventSink<AccountOverviewEvent>()

    private val _isEditMode = MutableStateFlow(false)
    private val _showEditButton = MutableStateFlow(false)
    private val configuredKeys by lazy { account.configuration.all() }

    private val editButton = AccountEditButton(
        visible = combine(_isEditMode, _showEditButton) { isEdit, showEdit -> isEdit || showEdit },
        isEditMode = _isEditMode,
        onClick = { _isEditMode.update { !it } },
    )

    private val scaffoldState = MutableSpeziScaffoldState(
        coroutinesLauncher = coroutinesLauncher,
        appBar = speziAppBar {
            title(Strings.account_overview_title)
            back { eventSink.push(AccountOverviewEvent.Dismissed) }
            action(editButton)
        }
    )

    private val uiState = MutableStateFlow<AccountOverviewState>(AccountOverviewState.Loading)

    val screen = AccountOverviewScreen(
        scaffoldState = scaffoldState,
        state = uiState.asStateFlow(),
    )

    val events = eventSink.source()

    init {
        viewModelScope.launch {
            merge(
                account.details,
                _isEditMode,
            ).collect {
                val details = account.details.value
                if (details == null) {
                    eventSink.push(AccountOverviewEvent.Dismissed)
                    return@collect
                }
                val hasAbsentOptionalKeys = account.configuration
                    .all(filteredBy = setOf(AccountKeyRequirement.COLLECTED, AccountKeyRequirement.SUPPORTED))
                    .any { key ->
                        key !== AccountKeys.password &&
                            key.display != null &&
                            key.entry != null &&
                            key.options.contains(AccountKeyOptions.Mutable) &&
                            details.getAnyOrNull(key::class) == null
                    }

                _showEditButton.update { hasAbsentOptionalKeys }
                // Auto-exit edit mode when there are no longer any absent optional keys to add.
                if (!_showEditButton.value) _isEditMode.update { false }
                val newLayout = buildLayout(details = details)
                uiState.update { AccountOverviewState.Content(layout = newLayout) }
            }
        }
    }

    private fun buildLayout(details: AccountDetails) = AccountOverviewLayout(
        title = StringResource(Strings.account_overview_section_title),
        header = buildHeader(details),
        sections = buildSections(details),
        logout = AsyncTextButton(
            title = StringResource(Strings.account_overview_logout_button),
            containerColor = { Colors.error },
            action = ::logout,
        ),
    )

    private fun buildHeader(details: AccountDetails): AccountProfileHeader {
        val name = details[AccountKeys.name::class]?.fullName.orEmpty()
        val userId = details[AccountKeys.userId::class]
        val email = details[AccountKeys.email::class] ?: userId
        val displayName = name.ifBlank { userId }
        val parts = displayName.trim().split("\\s+".toRegex())
        val initials = when {
            parts.isEmpty() -> null
            parts.size == 1 -> parts[0].first().uppercase()
            else -> parts[0].first().uppercase() + parts[1].first().uppercase()
        }
        return AccountProfileHeader(
            initials = initials,
            name = displayName,
            description = email,
        )
    }

    private fun buildSections(details: AccountDetails): List<AccountOverviewSection> {
        val sections = mutableListOf<AccountOverviewSection>()

        // Default section one row for name/identity details, one for sign-in & security.
        val defaultSectionItems = buildList {
            buildNameDetails(details)?.let { add(it) }
            buildSecurityItem(details)?.let { add(it) }
        }
        if (defaultSectionItems.isNotEmpty()) {
            sections.add(AccountOverviewSection(title = null, items = defaultSectionItems))
        }

        // remaining sections (excluding .name and .credentials as covered by default section).
        account.configuration
            .allCategorized(
                filteredBy = setOf(
                    AccountKeyRequirement.REQUIRED,
                    AccountKeyRequirement.COLLECTED,
                    AccountKeyRequirement.SUPPORTED,
                ),
                requiredOptions = AccountKeyOptions.Display,
            )
            .filter { (category, _) ->
                category != AccountKeyCategory.Name && category != AccountKeyCategory.Credentials
            }
            .mapNotNull { (category, keys) ->
                val items = keys.mapNotNull { key -> buildSectionItem(key, details) }
                if (items.isEmpty()) return@mapNotNull null
                AccountOverviewSection(
                    title = category.title,
                    items = items,
                )
            }
            .forEach { sections.add(it) }

        return sections
    }

    private fun buildNameDetails(details: AccountDetails): AccountOverviewItem<String>? {
        val isAnonymousUser = details.isAnonymousUser
        val displaysNameDetails = (account.configuration[AccountKeys.userId::class] != null && !isAnonymousUser) ||
            configuredKeys.any { it.category == AccountKeyCategory.Name }
        if (!displaysNameDetails) return null
        val userIdLabel = details.userIdType.label
        val title = when {
            account.configuration[AccountKeys.name::class] == null -> userIdLabel
            isAnonymousUser -> AccountKeys.name.name
            else -> AccountKeys.name.name + StringResource(", ") + userIdLabel
        }
        return AccountOverviewItem(
            title = title,
            valueDisplay = StringDataDisplay(),
            value = "",
            leadingImage = ImageResource(Icons.Default.AccountCircle),
            showArrow = true,
            onClick = {
                showSheet(
                    AccountSheetRequest.NameOverview(
                        title = title,
                        onEvent = { scaffoldState.dismissBottomSheet() },
                    )
                )
            },
        )
    }

    private fun buildSecurityItem(details: AccountDetails): AccountOverviewItem<String>? {
        val isAnonymousUser = details.isAnonymousUser
        val displaysSecurityItem = !isAnonymousUser &&
            configuredKeys.any { it.category == AccountKeyCategory.Credentials && !it.isHiddenCredential }
        if (!displaysSecurityItem) return null
        return AccountOverviewItem(
            title = StringResource(Strings.account_overview_sign_in_security),
            valueDisplay = StringDataDisplay(),
            value = "",
            leadingImage = ImageResource(Icons.Default.Lock),
            showArrow = true,
            onClick = {
                showSheet(AccountSheetRequest.SecurityOverview(onEvent = { scaffoldState.dismissBottomSheet() }))
            },
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V : Any> buildSectionItem(
        key: AccountKey<V>,
        details: AccountDetails,
    ): AccountOverviewItem<V>? {
        val display = key.display
        val currentValue = details.getAnyOrNull(key::class) as? V
        return when {
            display == null -> null
            currentValue != null -> {
                val isEditable = key.entry != null && key.options.contains(AccountKeyOptions.Mutable)
                AccountOverviewItem(
                    title = key.name,
                    valueDisplay = display,
                    value = currentValue,
                    leadingImage = null,
                    showArrow = isEditable,
                    onClick = if (isEditable) { { openEditKey(key, currentValue) } } else null,
                )
            }
            !_isEditMode.value || key.entry == null || !key.options.contains(AccountKeyOptions.Mutable) -> null
            else -> {
                val initialValue = key.initialValue.value
                AccountOverviewItem(
                    title = key.name,
                    valueDisplay = display,
                    value = initialValue,
                    leadingImage = null,
                    showArrow = true,
                    onClick = { openEditKey(key, initialValue) },
                )
            }
        }
    }

    private fun openEditKey(key: AnyAccountKey, value: Any) {
        showSheet(
            AccountSheetRequest.EditKey(
                key = key,
                currentValue = value,
                onEvent = { scaffoldState.dismissBottomSheet() },
            )
        )
    }

    private fun showSheet(sheetRequest: AccountSheetRequest) {
        val screen = accountSheetController.getSheet(
            request = sheetRequest,
            coroutinesLauncher = coroutinesLauncher,
        )
        scaffoldState.showBottomSheet(screen)
    }

    private suspend fun logout() {
        account.service.logout()
            .onSuccess { eventSink.push(AccountOverviewEvent.Dismissed) }
            .onFailure {
                scaffoldState.showErrorToast(
                    message = StringResource(Strings.account_overview_logout_failed),
                )
            }
    }
}

internal data class AccountOverviewScreen(
    val scaffoldState: SpeziScaffoldState,
    val state: StateFlow<AccountOverviewState>,
)

internal sealed interface AccountOverviewState {
    val layout: ComposableContent

    data object Loading : AccountOverviewState {
        override val layout = LoadingLayout()
    }

    data class Content(override val layout: AccountOverviewLayout) : AccountOverviewState
}

internal sealed interface AccountOverviewEvent {
    data object Dismissed : AccountOverviewEvent
}
