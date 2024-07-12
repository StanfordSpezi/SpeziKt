package edu.stanford.spezi.module.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.CredentialRegisterManagerAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject internal constructor(
    private val credentialRegisterManagerAuth: CredentialRegisterManagerAuth,
    private val messageNotifier: MessageNotifier,
    private val accountEvents: AccountEvents,
    private val validator: RegisterFormValidator,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private var googleCredential: String? = null

    private val birthdayDateFormatter by lazy {
        DateTimeFormatter.ofPattern(
            "dd MMMM yyyy",
            Locale.getDefault()
        )
    }

    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.TextFieldUpdate -> {
                    val newValue = FieldState(action.newValue)
                    val updatedUiState = when (action.type) {
                        TextFieldType.EMAIL -> it.copy(email = newValue)
                        TextFieldType.PASSWORD -> it.copy(password = newValue)
                        TextFieldType.FIRST_NAME -> it.copy(firstName = newValue)
                        TextFieldType.LAST_NAME -> it.copy(lastName = newValue)
                        TextFieldType.GENDER -> it.copy(selectedGender = newValue)
                    }
                    updatedUiState.copy(
                        isFormValid = validator.isFormValid(updatedUiState),
                        isRegisterButtonEnabled = isRegisterButtonEnabled(updatedUiState)
                    )
                }

                is Action.DateFieldUpdate -> {
                    val updatedUiState = it.copy(
                        dateOfBirth = action.newValue,
                        formattedDateOfBirth = birthdayDateFormatter.format(action.newValue),
                        isDatePickerDialogOpen = false,
                    )
                    updatedUiState.copy(
                        isFormValid = validator.isFormValid(updatedUiState),
                        isRegisterButtonEnabled = isRegisterButtonEnabled(updatedUiState)
                    )
                }

                is Action.DropdownMenuExpandedUpdate -> {
                    it.copy(isDropdownMenuExpanded = action.isExpanded)
                }

                is Action.OnRegisterPressed -> {
                    onRegisteredPressed()
                }

                is Action.SetIsGoogleSignUp -> {
                    if (action.isGoogleSignUp) {
                        initializeGoogleSignUp()
                    }
                    it.copy(isGoogleSignUp = action.isGoogleSignUp)
                }

                is Action.TogglePasswordVisibility -> {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }

                is Action.SetIsDatePickerOpen -> {
                    it.copy(isDatePickerDialogOpen = action.isOpen)
                }
            }
        }
    }

    private fun initializeGoogleSignUp() {
        viewModelScope.launch {
            val credential = credentialRegisterManagerAuth.getGoogleSignUpCredential()
            credential?.let {
                onAction(
                    Action.TextFieldUpdate(
                        it.displayName.toString().split(" ")[0],
                        TextFieldType.FIRST_NAME,
                    )
                )
                onAction(
                    Action.TextFieldUpdate(
                        it.displayName.toString().split(" ")[1],
                        TextFieldType.LAST_NAME,
                    )
                )
                googleCredential = it.idToken
            } ?: run {
                messageNotifier.notify("Failed to get Google account")
                _uiState.update {
                    // swap to email sign up when google fails
                    it.copy(isGoogleSignUp = false)
                }
            }
        }
    }

    private fun onRegisteredPressed(): RegisterUiState {
        val uiState = _uiState.value
        return if (validator.isFormValid(uiState)) {
            viewModelScope.launch {
                if (uiState.isGoogleSignUp) {
                    logger.i { "Google sign up: $googleCredential" }
                    credentialRegisterManagerAuth.googleSignUp(
                        idToken = googleCredential!!,
                        firstName = uiState.firstName.value,
                        lastName = uiState.lastName.value,
                        selectedGender = uiState.selectedGender.value,
                        dateOfBirth = uiState.dateOfBirth!!,
                        email = uiState.email.value,
                    )
                } else {
                    credentialRegisterManagerAuth.passwordAndEmailSignUp(
                        email = uiState.email.value,
                        password = uiState.password.value,
                        firstName = uiState.firstName.value,
                        lastName = uiState.lastName.value,
                        selectedGender = uiState.selectedGender.value,
                        dateOfBirth = uiState.dateOfBirth!!,
                    )
                }.onSuccess {
                    accountEvents.emit(AccountEvents.Event.SignUpSuccess)
                }.onFailure {
                    accountEvents.emit(AccountEvents.Event.SignUpFailure)
                    messageNotifier.notify("Failed to sign up")
                }
            }
            uiState
        } else {
            uiState.copy(
                email = uiState.email.copy(
                    error = validator.isValidEmail(uiState.email.value).errorMessageOrNull()
                ),
                password = uiState.password.copy(
                    error = validator.isValidPassword(uiState.password.value).errorMessageOrNull()
                ),
                firstName = uiState.firstName.copy(
                    error = validator.firstnameResult(uiState.firstName.value).errorMessageOrNull()
                ),
                lastName = uiState.lastName.copy(
                    error = validator.lastnameResult(uiState.lastName.value).errorMessageOrNull()
                ),
                selectedGender = uiState.selectedGender.copy(
                    error = validator.isGenderValid(uiState.selectedGender.value)
                        .errorMessageOrNull()
                ),
                dateOfBirthError = validator.birthdayResult(uiState.dateOfBirth)
                    .errorMessageOrNull(),
                isFormValid = validator.isFormValid(uiState)
            )
        }
    }

    private fun isRegisterButtonEnabled(uiState: RegisterUiState): Boolean {
        return uiState.email.value.isNotEmpty() &&
            uiState.firstName.value.isNotEmpty() &&
            uiState.lastName.value.isNotEmpty() &&
            uiState.selectedGender.value.isNotEmpty() &&
            uiState.dateOfBirth != null
    }
}
