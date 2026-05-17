package edu.stanford.spezi.account.internal.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import edu.stanford.spezi.account.Account
import edu.stanford.spezi.account.AccountDetails
import edu.stanford.spezi.account.AccountIdKey
import edu.stanford.spezi.account.AccountKey
import edu.stanford.spezi.account.AccountKeyCategory
import edu.stanford.spezi.account.AccountKeyRequirement
import edu.stanford.spezi.account.AccountKeyType
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.AccountService
import edu.stanford.spezi.account.AnyAccountKey
import edu.stanford.spezi.account.AnyAccountKeyType
import edu.stanford.spezi.account.UserIdKey
import edu.stanford.spezi.account.UserIdType
import edu.stanford.spezi.account.fieldValidationRules
import edu.stanford.spezi.account.instance
import edu.stanford.spezi.account.isAnonymousUser
import edu.stanford.spezi.account.keys
import edu.stanford.spezi.account.requiredAccountKeys
import edu.stanford.spezi.account.userIdConfiguration
import edu.stanford.spezi.core.coroutines.CoroutinesLauncher
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.MutableSpeziScaffoldState
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.SignUpFormEntry
import edu.stanford.spezi.ui.account.SignUpFormLayout
import edu.stanford.spezi.ui.account.SignUpSection
import edu.stanford.spezi.ui.showErrorToast
import edu.stanford.spezi.ui.speziAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Builds and manages the sign-up sheet.
 *
 * Derives the visible form fields from the account configuration and service requirements,
 * handles per-field validation, and submits the completed [AccountDetails] via [AccountService].
 * Used by [AccountSheetController] when the user initiates sign-up from the login screen.
 */
internal class AccountSignUpSheetController(
    private val account: Account,
    private val accountService: AccountService,
) {
    private val config = accountService.configuration

    private val signUpFormState by lazy { MutableStateFlow(SignUpFormState(fields = emptyMap())) }
    private val signUpKeysByCategory: Map<AccountKeyCategory, List<AnyAccountKey>> by lazy {
        buildSignUpKeysByCategory()
    }
    private val signUpFormKeys: List<AnyAccountKey>
        get() = signUpKeysByCategory.values.flatten()

    fun buildSheet(
        request: AccountSheetRequest.SignUp,
        coroutinesLauncher: CoroutinesLauncher,
    ): AccountSheet {
        signUpFormState.update {
            SignUpFormState(
                fields = signUpFormKeys.associate { key ->
                    key::class to SignUpFieldState(value = key.initialValue.value)
                }
            )
        }
        val scaffoldState = MutableSpeziScaffoldState(
            coroutinesLauncher = coroutinesLauncher,
            appBar = speziAppBar {
                close { request.onEvent(AccountSheetEvent.Dismissed) }
            }
        )
        return AccountSheet(
            scaffoldState = scaffoldState,
            layout = signUpFormState.map {
                buildSignUpLayout(
                    signUpState = it,
                    scaffoldState = scaffoldState,
                    onEvent = { event -> request.onEvent(event) }
                )
            },
            onDismiss = { request.onEvent(AccountSheetEvent.Dismissed) },
            draggable = false,
        )
    }

    // Email is excluded when UserIdType.Email AND userId IS present in the signup form,
    // because in that case userId's entry already captures the email address.
    private fun AnyAccountKey.isSignUpExcluded(): Boolean {
        if (this != AccountKeys.email || config.userIdConfiguration.idType != UserIdType.Email) return false
        val userIdShownViaService = config.requiredAccountKeys.keys().any { it == AccountKeys.userId }
        val userIdShownViaConfig = account.configuration[UserIdKey::class]
            ?.requirement
            ?.let { it == AccountKeyRequirement.REQUIRED || it == AccountKeyRequirement.COLLECTED }
            ?: false
        return userIdShownViaService || userIdShownViaConfig
    }

    private fun buildSignUpKeysByCategory(): Map<AccountKeyCategory, List<AnyAccountKey>> {
        val result: MutableMap<AccountKeyCategory, MutableList<AnyAccountKey>> = account.configuration
            .allCategorized(
                filteredBy = setOf(AccountKeyRequirement.REQUIRED, AccountKeyRequirement.COLLECTED),
            )
            .mapValues { (_, keys) -> keys.filter { it.entry != null && !it.isSignUpExcluded() }.toMutableList() }
            .filter { (_, keys) -> keys.isNotEmpty() }
            .toMutableMap()

        config.requiredAccountKeys.keys().forEach { key ->
            if (!key.isSignUpExcluded()) {
                val current = result.getOrPut(key.category) { mutableListOf() }
                if (current.none { it == key }) current.add(key)
            }
        }

        val anonymousDetails = account.details.value
        if (anonymousDetails != null && anonymousDetails.isAnonymousUser) {
            result.entries.removeIf { (_, keys) ->
                @Suppress("UNCHECKED_CAST")
                keys.removeAll { anonymousDetails.contains(it as AccountKey<Any>) }
                keys.isEmpty()
            }
        }

        return result
    }

    private fun buildSignUpLayout(
        signUpState: SignUpFormState,
        scaffoldState: MutableSpeziScaffoldState,
        onEvent: (AccountSheetEvent) -> Unit,
    ): SignUpFormLayout {
        val sections = signUpKeysByCategory
            .mapNotNull { (category, keys) ->
                val entries = keys.mapNotNull { key -> buildSignUpEntry(key, signUpState) }
                if (entries.isEmpty()) return@mapNotNull null
                SignUpSection(
                    title = category.title,
                    entries = entries,
                )
            }

        return SignUpFormLayout(
            headerIcon = ImageResource(Icons.Default.AccountCircle),
            title = StringResource(Strings.account_sign_up_title),
            description = StringResource(Strings.account_sign_up_description),
            sections = sections,
            signUpButton = AsyncTextButton(
                title = StringResource(Strings.account_sign_up_button),
                action = { submitSignUpForm(scaffoldState = scaffoldState, onEvent = onEvent) },
            ),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V : Any> buildSignUpEntry(key: AccountKey<V>, state: SignUpFormState): SignUpFormEntry<V>? {
        val entry = key.entry
        val fieldState = state.fields[key::class]
        val currentValue = fieldState?.value as? V

        if (entry == null || fieldState == null || currentValue == null) return null

        return SignUpFormEntry(
            title = accountService.displayName(key),
            entry = accountService.requireEntryComposable(key),
            value = currentValue,
            validationMessage = fieldState.error,
            onValueChange = { newValue ->
                signUpFormState.update { current ->
                    current.copy(
                        fields = current.fields + (key::class to SignUpFieldState(value = newValue))
                    )
                }
            },
        )
    }

    private suspend fun submitSignUpForm(scaffoldState: MutableSpeziScaffoldState, onEvent: (AccountSheetEvent) -> Unit) {
        val state = signUpFormState.value
        val errors = mutableMapOf<AnyAccountKeyType, StringResource>()

        state.fields.forEach { (keyType, fieldState) ->
            val key = keyType.instance()
            if (key.valueType == String::class) {
                @Suppress("UNCHECKED_CAST")
                val stringKeyType = keyType as AccountKeyType<String>
                val value = fieldState.value as? String ?: ""
                val error = config
                    .fieldValidationRules(stringKeyType)
                    ?.firstNotNullOfOrNull { it.validate(value)?.message }
                if (error != null) errors[keyType] = error
            }
        }

        if (errors.isNotEmpty()) {
            signUpFormState.update { current ->
                current.copy(
                    fields = current.fields.mapValues { (keyType, fieldState) ->
                        fieldState.copy(error = errors[keyType])
                    }
                )
            }
            return
        }

        val details = AccountDetails()
        state.fields.forEach { (keyType, fieldState) ->
            details.setAny(keyType, fieldState.value)
        }

        if (!details.contains(AccountKeys.accountId)) {
            val userId = state.fields[UserIdKey::class]?.value as? String
            if (userId != null) details[AccountIdKey::class] = userId
        }

        details.validateAgainstSignupRequirements(account.configuration)
            .mapCatching {
                accountService.signUp(details).getOrThrow()
            }
            .onSuccess {
                onEvent(AccountSheetEvent.Success)
            }
            .onFailure {
                scaffoldState.showErrorToast(message = StringResource(Strings.account_sign_up_failed))
            }
    }

    private data class SignUpFieldState(
        val value: Any,
        val error: StringResource? = null,
    )

    private data class SignUpFormState(
        val fields: Map<AnyAccountKeyType, SignUpFieldState>,
    )
}
