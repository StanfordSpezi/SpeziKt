package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScreenEvents: AppScreenEvents,
    private val phoneNumberService: PhoneNumberService,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {
    private var allCountryCodeUiModels: List<CountryCodeUiModel>? = null
    private val phoneNumberUtil by lazy { PhoneNumberUtil.getInstance() }

    private var selectedCountryCode = CountryCode(
        name = "United States",
        iso = "US",
        dialCode = "+1",
        emoji = "\uD83C\uDDFA\uD83C\uDDF8"
    )

    private val _uiState = MutableStateFlow(getInitialState())
    val uiState = _uiState.asStateFlow()

    private suspend fun startPhoneNumberVerification() {
        val inputStep = uiState.value.step as? PhoneNumberInputUiModel ?: return

        val phoneNumber = "${selectedCountryCode.dialCode}${inputStep.phoneNumber}"
        phoneNumberService.startPhoneNumberVerification(phoneNumber)
            .onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
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
                }
            }.onFailure {
                messageNotifier.notify(R.string.error_sending_verification_message_message)
            }
    }

    private suspend fun checkPhoneNumberVerification() {
        val verificationStep = uiState.value.step as? VerificationCodeUiModel ?: return
        val verificationCode = verificationStep.digits.joinToString(separator = "")
        phoneNumberService.checkPhoneNumberVerification(code = verificationCode, phoneNumber = verificationStep.phoneNumber)
            .onSuccess {
                messageNotifier.notify(R.string.phone_number_verification_success_message)
                dismissFlow()
            }.onFailure {
                messageNotifier.notify(R.string.error_checking_verification_code_message)
            }
    }

    private fun onVerificationCodeDigitChanged(index: Int, value: String) {
        _uiState.update { currentState ->
            if (currentState.step is VerificationCodeUiModel) {
                val step = currentState.step
                val digits = step.digits.toMutableList()
                val newValue = value.toIntOrNull()
                val isValid = newValue != null && newValue in VALID_VERIFICATION_CODE_DIGIT_RANGE
                val nextFocusedIndex = if (isValid) index + 1 else index
                digits[index] = newValue.takeIf { isValid }
                currentState.copy(
                    step = step.copy(digits = digits, focusedIndex = nextFocusedIndex),
                    actionButton = currentState.actionButton.copy(enabled = digits.none { it == null })
                )
            } else {
                currentState
            }
        }
    }

    private fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { currentState ->
            val step = currentState.step
            if (step is PhoneNumberInputUiModel) {
                val isValid = runCatching {
                    val number = phoneNumberUtil.parse(phoneNumber, selectedCountryCode.iso.uppercase())
                    phoneNumberUtil.isValidNumber(number)
                }.getOrDefault(false)
                val errorMessage = if (isValid || phoneNumber.isEmpty()) null else StringResource(R.string.invalid_phone_number_message)
                currentState.copy(
                    step = step.copy(
                        phoneNumber = phoneNumber,
                        errorMessage = errorMessage,
                    ),
                    actionButton = currentState.actionButton.copy(enabled = isValid)
                )
            } else {
                currentState
            }
        }
    }

    private fun updatePhoneNumberInputStep(block: (PhoneNumberInputUiModel) -> PhoneNumberInputUiModel) {
        _uiState.update { currentState ->
            if (currentState.step is PhoneNumberInputUiModel) {
                currentState.copy(step = block(currentState.step))
            } else {
                currentState
            }
        }
    }

    private fun showCountryCodeSelectionBottomSheet() {
        allCountryCodeUiModels = allCountryCodeUiModels ?: loadCountryCodes().codes.map { code ->
            CountryCodeUiModel(
                emojiFlag = code.emoji,
                countryCode = code.iso,
                number = code.dialCode,
                countryName = code.name,
                onClick = {
                    selectedCountryCode = code
                    _uiState.update { currentState ->
                        if (currentState.step is PhoneNumberInputUiModel) {
                            currentState.copy(
                                step = currentState.step.copy(
                                    countryCodeButtonTitle = "${code.emoji} ${code.dialCode}",
                                    phoneNumber = "",
                                    countrySelection = null,
                                    errorMessage = null,
                                ),
                                actionButton = currentState.actionButton.copy(enabled = false),
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

    private fun dismissFlow() {
        _uiState.update { getInitialState() }
        appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
    }

    private fun loadCountryCodes() = context.resources.openRawResource(R.raw.country_codes)
        .bufferedReader()
        .use { it.readText() }
        .let { Json.decodeFromString<CountryCodes>(it) }

    private fun getInitialState(): PhoneNumberUiState {
        return PhoneNumberUiState(
            title = StringResource(R.string.account_settings_add_phone_number),
            onDismiss = ::dismissFlow,
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
                onCountryCodeButtonClicked = ::showCountryCodeSelectionBottomSheet,
                countrySelection = null,
            )
        )
    }

    private companion object {
        const val VERIFICATION_CODE_SIZE = 6
        val VALID_VERIFICATION_CODE_DIGIT_RANGE = 0..9
    }

    @Serializable
    private data class CountryCodes(
        val codes: List<CountryCode>,
    )
}
