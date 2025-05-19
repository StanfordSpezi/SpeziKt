package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.navigation.NavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val phoneNumberService: PhoneNumberService,
    private val messageNotifier: MessageNotifier,
    private val navigator: Navigator,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {
    private var allCountryCodeUiModels: List<CountryCodeUiModel>? = null

    private var selectedCountryCode = CountryCode(
        name = "United States",
        iso = "US",
        dialCode = "+1",
        emoji = "\uD83C\uDDFA\uD83C\uDDF8"
    )

    private val _uiState = MutableStateFlow(
        PhoneNumberSettingsUiState(
            onAddPhoneNumberClicked = ::displayBottomSheet,
            onBackClicked = { navigator.navigateTo(NavigationEvent.PopBackStack) },
            phoneNumbers = emptyList(),
            bottomSheet = null
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        observeUserPhoneNumbers()
    }

    private fun observeUserPhoneNumbers() {
        viewModelScope.launch {
            userSessionManager.observeRegisteredUser()
                .map { it.phoneNumbers.map { number -> mapPhoneNumber(phoneNumber = number) } }
                .collect { phoneNumbers ->
                    _uiState.update { it.copy(phoneNumbers = phoneNumbers) }
                }
        }
    }

    private fun mapPhoneNumber(phoneNumber: String) = PhoneNumberUiModel(
        phoneNumber = phoneNumberService.format(phoneNumber),
        coroutineScope = { viewModelScope },
        onDeleteClicked = {
            phoneNumberService.deletePhoneNumber(phoneNumber)
                .onFailure {
                    messageNotifier.notify(R.string.phone_number_deletion_error_message)
                }
        }
    )

    private suspend fun startPhoneNumberVerification() {
        val inputStep = uiState.value.bottomSheet?.step as? PhoneNumberInputUiModel ?: return

        val phoneNumber = "${selectedCountryCode.dialCode}${inputStep.phoneNumber}"
        phoneNumberService.startPhoneNumberVerification(phoneNumber)
            .onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        bottomSheet = currentState.bottomSheet?.copy(
                            title = StringResource(R.string.enter_verification_code_title),
                            step = VerificationCodeUiModel(
                                description = StringResource(R.string.enter_verification_code_description),
                                phoneNumber = phoneNumber,
                                digits = List(VERIFICATION_CODE_SIZE) { null },
                                focusedIndex = 0,
                                onValueChanged = ::onVerificationCodeDigitChanged,
                            ),
                            actionButton = AsyncTextButton(
                                title = context.getString(R.string.verify_phone_number_button_title),
                                enabled = false,
                                coroutineScope = { viewModelScope },
                                action = ::checkPhoneNumberVerification,
                            )
                        )
                    )
                }
            }.onFailure {
                messageNotifier.notify(R.string.error_sending_verification_message_message)
            }
    }

    private suspend fun checkPhoneNumberVerification() {
        val verificationStep = uiState.value.bottomSheet?.step as? VerificationCodeUiModel ?: return
        val verificationCode = verificationStep.digits.joinToString(separator = "")
        phoneNumberService.checkPhoneNumberVerification(code = verificationCode, phoneNumber = verificationStep.phoneNumber)
            .onSuccess {
                messageNotifier.notify(R.string.phone_number_verification_success_message)
                dismissBottomSheet()
            }.onFailure {
                messageNotifier.notify(R.string.error_checking_verification_code_message)
            }
    }

    private fun onVerificationCodeDigitChanged(index: Int, value: String) {
        _uiState.update { currentState ->
            val step = currentState.bottomSheet?.step
            if (step is VerificationCodeUiModel) {
                val digits = step.digits.toMutableList()
                val newValue = value.toIntOrNull()
                val isValid = newValue != null && newValue in VALID_VERIFICATION_CODE_DIGIT_RANGE
                val nextFocusedIndex = if (isValid) index + 1 else index
                digits[index] = newValue.takeIf { isValid }
                currentState.copy(
                    bottomSheet = currentState.bottomSheet.copy(
                        step = step.copy(digits = digits, focusedIndex = nextFocusedIndex),
                        actionButton = currentState.bottomSheet.actionButton.copy(enabled = digits.none { it == null })
                    )
                )
            } else {
                currentState
            }
        }
    }

    private fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { currentState ->
            val step = currentState.bottomSheet?.step
            if (step is PhoneNumberInputUiModel) {
                val isValid = phoneNumberService.isPhoneNumberValid(phoneNumber, selectedCountryCode.iso)
                val errorMessage = if (isValid || phoneNumber.isEmpty()) null else StringResource(R.string.invalid_phone_number_message)
                currentState.copy(
                    bottomSheet = currentState.bottomSheet.copy(
                        step = step.copy(
                            phoneNumber = phoneNumber,
                            errorMessage = errorMessage,
                        ),
                        actionButton = currentState.bottomSheet.actionButton.copy(enabled = isValid)
                    )
                )
            } else {
                currentState
            }
        }
    }

    private fun updatePhoneNumberInputStep(block: (PhoneNumberInputUiModel) -> PhoneNumberInputUiModel) {
        _uiState.update { currentState ->
            val step = currentState.bottomSheet?.step
            if (step is PhoneNumberInputUiModel) {
                currentState.copy(bottomSheet = currentState.bottomSheet.copy(step = block(step)))
            } else {
                currentState
            }
        }
    }

    private suspend fun showCountryCodeSelectionBottomSheet() {
        allCountryCodeUiModels = allCountryCodeUiModels ?: phoneNumberService.getAllCountryCodes().map { code ->
            CountryCodeUiModel(
                emojiFlag = code.emoji,
                countryCode = code.iso,
                number = code.dialCode,
                countryName = code.name,
                onClick = {
                    selectedCountryCode = code
                    _uiState.update { currentState ->
                        val step = currentState.bottomSheet?.step
                        if (step is PhoneNumberInputUiModel) {
                            currentState.copy(
                                bottomSheet = currentState.bottomSheet.copy(
                                    step = step.copy(
                                        countryCodeButtonTitle = "${code.emoji} ${code.dialCode}",
                                        phoneNumber = "",
                                        countrySelection = null,
                                        errorMessage = null,
                                    ),
                                    actionButton = currentState.bottomSheet.actionButton.copy(enabled = false),
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }
            )
        }

        val countryCodeSelectionUiModel = CountryCodeSelectionUiModel(
            searchQuery = "",
            onSearchQueryChanged = ::onSearchQueryChanged,
            items = allCountryCodeUiModels ?: emptyList(),
            onDismiss = { updatePhoneNumberInputStep { step -> step.copy(countrySelection = null) } }
        )

        updatePhoneNumberInputStep { step -> step.copy(countrySelection = countryCodeSelectionUiModel) }
    }

    private fun onSearchQueryChanged(query: String) {
        val filteredItems = allCountryCodeUiModels?.filter { code ->
            "${code.countryName}${code.countryCode}".contains(query, ignoreCase = true)
        } ?: emptyList()

        updatePhoneNumberInputStep { step ->
            step.copy(countrySelection = step.countrySelection?.copy(items = filteredItems, searchQuery = query))
        }
    }

    private fun dismissBottomSheet() {
        _uiState.update { it.copy(bottomSheet = null) }
    }

    private fun displayBottomSheet() {
        _uiState.update { it.copy(bottomSheet = createAddPhoneNumberBottomSheet()) }
    }

    private fun createAddPhoneNumberBottomSheet(): AddPhoneNumberBottomSheet {
        return AddPhoneNumberBottomSheet(
            title = StringResource(R.string.phone_number_add),
            onDismiss = ::dismissBottomSheet,
            actionButton = AsyncTextButton(
                title = context.getString(R.string.send_verification_message_button_title),
                enabled = false,
                coroutineScope = { viewModelScope },
                action = ::startPhoneNumberVerification,
            ),
            step = PhoneNumberInputUiModel(
                phoneNumber = "",
                onPhoneNumberChanged = ::onPhoneNumberChanged,
                errorMessage = null,
                countryCodeButtonTitle = "${selectedCountryCode.emoji} ${selectedCountryCode.dialCode}",
                onCountryCodeButtonClicked = {
                    viewModelScope.launch { showCountryCodeSelectionBottomSheet() }
                },
                countrySelection = null,
            )
        )
    }

    private companion object {
        const val VERIFICATION_CODE_SIZE = 6
        val VALID_VERIFICATION_CODE_DIGIT_RANGE = 0..9
    }
}
