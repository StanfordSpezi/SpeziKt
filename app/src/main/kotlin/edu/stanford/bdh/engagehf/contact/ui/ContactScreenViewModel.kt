package edu.stanford.bdh.engagehf.contact.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.contact.data.EngageContactRepository
import edu.stanford.spezi.contact.Contact
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.navigation.NavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ContactScreenViewModel @Inject constructor(
    private val engageContactRepository: EngageContactRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            engageContactRepository.getContact().fold(onSuccess = { contact ->
                _uiState.value = UiState.ContactLoaded(contact)
            }, onFailure = { error ->
                _uiState.value = UiState.Error(StringResource(R.string.generic_error_description))
                logger.e(error) { "Failed to load contact" }
            })
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.Back -> navigator.navigateTo(NavigationEvent.PopBackStack)
        }
    }

    sealed interface Action {
        data object Back : Action
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Error(val message: StringResource) : UiState
        data class ContactLoaded(val contact: Contact) : UiState
    }
}
