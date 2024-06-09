package edu.stanford.spezi.module.account.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.ActionProvider
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.cred.manager.CredentialRegisterManagerAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject internal constructor(
    private val navigator: Navigator,
    private val credentialRegisterManagerAuth: CredentialRegisterManagerAuth,
    @ApplicationContext private val appContext: Context,
    @Named("account-register") private val actionProvider: ActionProvider,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private var googleCredential: String? = null

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
                    updatedUiState.copy(isFormValid = isFormValid(updatedUiState))
                }

                is Action.DateFieldUpdate -> {
                    val updatedUiState = it.copy(dateOfBirth = action.newValue)
                    updatedUiState.copy(isFormValid = isFormValid(updatedUiState))
                }

                is Action.DropdownMenuExpandedUpdate -> {
                    it.copy(isDropdownMenuExpanded = action.isExpanded)
                }

                Action.OnRegisterPressed -> {
                    if (isFormValid(it)) {
                        viewModelScope.launch {
                            val result = if (it.isGoogleSignUp) {
                                logger.i { "Google sign up: $googleCredential" }
                                credentialRegisterManagerAuth.googleSignUp(
                                    idToken = googleCredential!!,
                                    firstName = uiState.value.firstName.value,
                                    lastName = uiState.value.lastName.value,
                                    selectedGender = uiState.value.selectedGender.value,
                                    dateOfBirth = uiState.value.dateOfBirth!!,
                                    email = uiState.value.email.value,
                                )
                            } else {
                                credentialRegisterManagerAuth.passwordAndEmailSignUp(
                                    email = uiState.value.email.value,
                                    password = uiState.value.password.value,
                                    firstName = uiState.value.firstName.value,
                                    lastName = uiState.value.lastName.value,
                                    selectedGender = uiState.value.selectedGender.value,
                                    dateOfBirth = uiState.value.dateOfBirth!!,
                                )
                            }
                            if (result.isSuccess) {
                                actionProvider.provideContinueButtonAction().invoke()
                            } else {
                                Toast.makeText(appContext, "Failed to sign up", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                    it.copy(
                        email = it.email.copy(error = if (isEmailValid(it.email.value)) null else "Invalid email"),
                        password = it.password.copy(
                            error = if (isPasswordValid(it.password.value)) {
                                null
                            } else {
                                "Password must be at least 6 characters"
                            }
                        ),
                        firstName = it.firstName.copy(
                            error = if (isFirstNameValid(it.firstName.value)) {
                                null
                            } else {
                                "First name cannot be empty"
                            }
                        ),
                        lastName = it.lastName.copy(
                            error = if (isLastNameValid(it.lastName.value)) {
                                null
                            } else {
                                "Last name cannot be empty"
                            }
                        ),
                        selectedGender = it.selectedGender.copy(
                            error =
                            if (isGenderValid(it.selectedGender.value)) {
                                null
                            } else {
                                "Please select valid gender"
                            }
                        ),
                        dateOfBirthError = if (isDobValid(it.dateOfBirth)) {
                            null
                        } else {
                            "Please select valid date of birth"
                        },
                        isFormValid = isFormValid(it)
                    )
                }

                is Action.SetIsGoogleSignUp -> {
                    if (action.isGoogleSignUp) {
                        initializeGoogleSignUp()
                    }
                    it.copy(isGoogleSignUp = action.isGoogleSignUp)
                }
            }
        }
    }

    private fun initializeGoogleSignUp() {
        viewModelScope.launch {
            val credential = credentialRegisterManagerAuth.getGoogleSignUpCredential(appContext)
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
                Toast.makeText(appContext, "Failed to get Google account", Toast.LENGTH_SHORT)
                    .show()
                _uiState.update {
                    // swap to email sign up when google fails
                    it.copy(isGoogleSignUp = false)
                }
            }
        }
    }
}
