package edu.stanford.spezi.module.account.login

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.cred.manager.CredentialManagerAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val credentialManagerAuth: CredentialManagerAuth,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.TextFieldUpdate -> {
                    val newValue = action.newValue
                    when (action.type) {
                        TextFieldType.PASSWORD -> it.copy(password = newValue)
                        TextFieldType.EMAIL -> it.copy(email = newValue)
                    }
                }

                is Action.TogglePasswordVisibility -> {
                    it.copy(passwordVisibility = !it.passwordVisibility)
                }

                is Action.NavigateToRegister -> {
                    when (action.type) {
                        NavigationTarget.LOGIN -> {
                            passwordSignIn()
                            it
                        }

                        NavigationTarget.REGISTER -> {
                            navigator.navigateTo(AccountNavigationEvent.RegisterScreen)
                            it
                        }
                    }
                }

                is Action.GoogleSignIn -> {
                    googleSignIn(action.context)
                    it
                }
            }
        }
    }

    private fun passwordSignIn() {
        // TODO()
        navigator.navigateTo(DefaultNavigationEvent.BluetoothScreen)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun googleSignIn(context: Context) {
        credentialManagerAuth.handleSignIn(context, viewModelScope) {
            _uiState.update {
                it.copy(googleIdTokenCredential = it.googleIdTokenCredential)
            }
        }
    }
}