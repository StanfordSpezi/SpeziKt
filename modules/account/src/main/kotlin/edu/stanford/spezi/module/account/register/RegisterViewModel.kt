package edu.stanford.spezi.module.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.cred.manager.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject internal constructor(
    private val navigator: Navigator,
    private val firebaseAuthManager: FirebaseAuthManager,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val firebaseUser = firebaseAuthManager.getUserData()
        if (firebaseUser != null) {
            _uiState.update {
                RegisterUiState(
                    email = FieldState(value = firebaseUser.email ?: ""),
                    password = FieldState(value = ""),
                    firstName = FieldState(
                        value = (firebaseUser.displayName ?: firebaseUser).toString().split(" ")[0]
                    ),
                    lastName = FieldState(
                        value = (firebaseUser.displayName ?: firebaseUser).toString().split(" ")[1]
                    ),
                    selectedGender = FieldState(value = ""),
                    dateOfBirth = null,
                    dateOfBirthError = null,
                    isDropdownMenuExpanded = false,
                    isFormValid = false,
                )
            }
        }
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
                            firebaseAuthManager.signUp(
                                uiState.value.email.value,
                                uiState.value.password.value,
                                uiState.value.firstName.value,
                                uiState.value.lastName.value,
                                uiState.value.selectedGender.value,
                                uiState.value.dateOfBirth
                            )
                        }
                    }
                    it.copy(
                        email = it.email.copy(error = if (isEmailValid(it.email.value)) null else "Invalid email"),
                        password = it.password.copy(error = if (isPasswordValid(it.password.value)) null else "Password must be at least 6 characters"),
                        firstName = it.firstName.copy(error = if (isFirstNameValid(it.firstName.value)) null else "First name cannot be empty"),
                        lastName = it.lastName.copy(error = if (isLastNameValid(it.lastName.value)) null else "Last name cannot be empty"),
                        selectedGender = it.selectedGender.copy(error = if (isGenderValid(it.selectedGender.value)) null else "Please select valid gender"),
                        dateOfBirthError = if (isDobValid(it.dateOfBirth)) null else "Please select valid date of birth",
                        isFormValid = isFormValid(it)
                    )

                }

                is Action.SetIsGoogleSignIn -> it.copy(isGoogleSignIn = action.isGoogleSignIn)
            }
        }
    }
}