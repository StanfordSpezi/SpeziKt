package edu.stanford.bdh.engagehf.bluetooth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.bluetooth.measurements.MeasurementsRepository
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.bdh.engagehf.messages.MessageType
import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.notification.notifier.FirebaseMessage
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier.Companion.FIREBASE_MESSAGE_KEY
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class BluetoothViewModel @Inject internal constructor(
    private val bleService: EngageBLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val measurementsRepository: MeasurementsRepository,
    private val messageRepository: MessageRepository,
    private val appScreenEvents: AppScreenEvents,
    private val navigator: Navigator,
    private val engageEducationRepository: EngageEducationRepository,
    private val healthSummaryService: HealthSummaryService,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(UiState())

    val uiState = _uiState.asStateFlow()

    init {
        bleService.start()
        observeBleService()
        observeRecords()
        observeMessages()
    }

    private fun observeBleService() {
        viewModelScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received EngageBLEService state $state" }
                _uiState.update { it.copy(bluetooth = uiStateMapper.mapBleServiceState(state)) }
            }
        }

        viewModelScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    is EngageBLEServiceEvent.MeasurementReceived -> {
                        appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
                        _uiState.update {
                            val dialog = uiStateMapper.mapMeasurementDialog(event.measurement)
                            it.copy(measurementDialog = dialog)
                        }
                    }
                }
            }
        }
    }

    private fun observeRecords() {
        viewModelScope.launch {
            measurementsRepository.observeBloodPressureRecord().collect { result ->
                _uiState.update { it.copy(bloodPressure = uiStateMapper.mapBloodPressure(result)) }
            }
        }

        viewModelScope.launch {
            measurementsRepository.observeHeartRateRecord().collect { result ->
                _uiState.update { it.copy(heartRate = uiStateMapper.mapHeartRate(result)) }
            }
        }

        viewModelScope.launch {
            measurementsRepository.observeWeightRecord().collect { result ->
                _uiState.update { it.copy(weight = uiStateMapper.mapWeight(result)) }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageRepository.observeUserMessages().collect { messages ->
                _uiState.update {
                    it.copy(
                        messages = messages
                    )
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                handleConfirmMeasurementAction(action)
            }

            is Action.DismissDialog -> {
                _uiState.update {
                    it.copy(measurementDialog = it.measurementDialog.copy(isVisible = false))
                }
            }

            is Action.MessageItemClicked -> {
                viewModelScope.launch {
                    uiStateMapper.mapMessagesAction(action.message.action)
                        .onFailure { error ->
                            logger.e(error) { "Error while mapping action: ${action.message.action}" }
                        }
                        .onSuccess { messagesAction ->
                            onMessage(
                                messagesAction = messagesAction,
                                messageId = action.message.id,
                                isDismissible = action.message.isDismissible,
                            )
                        }
                }
            }

            is Action.ToggleExpand -> {
                handleToggleExpandAction(action)
            }

            is Action.PermissionGranted, is Action.Resumed -> {
                bleService.start()
            }

            is Action.Settings.AppSettings -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                launch(intent = intent)
            }

            is Action.Settings.BluetoothSettings -> {
                launch(intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }

            is Action.NewIntent -> handleNewIntent(action.intent)
        }
    }

    private fun handleNewIntent(intent: Intent) {
        val firebaseMessage =
            intent.getParcelableExtra(FIREBASE_MESSAGE_KEY, FirebaseMessage::class.java)
        firebaseMessage?.messageId?.let { messageId ->
            viewModelScope.launch {
                onAction(
                    Action.MessageItemClicked(
                        message = Message(
                            id = messageId, // Is needed to dismiss the message
                            type = MessageType.Unknown, // We don't need the type, since we directly use the action
                            title = "", // We don't need the title, since we directly use the action
                            action = firebaseMessage.action, // We directly use the action
                            isDismissible = firebaseMessage.isDismissible
                                ?: false
                        )
                    )
                )
            }
        }
    }

    private fun launch(intent: Intent) {
        runCatching {
            context.startActivity(intent.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        }.onFailure {
            logger.e(it) { "Failed to launch intent ${intent.action}" }
        }
    }

    private suspend fun onMessage(
        messagesAction: MessagesAction,
        messageId: String,
        isDismissible: Boolean,
    ) {
        when (messagesAction) {
            is MessagesAction.HealthSummaryAction -> {
                handleHealthSummaryAction(messageId)
            }

            is MessagesAction.MeasurementsAction -> {
                appScreenEvents.emit(AppScreenEvents.Event.DoNewMeasurement)
            }

            is MessagesAction.MedicationsAction -> {
                appScreenEvents.emit(
                    AppScreenEvents.Event.NavigateToTab(
                        BottomBarItem.MEDICATION
                    )
                )
            }

            is MessagesAction.QuestionnaireAction -> {
                navigator.navigateTo(
                    AppNavigationEvent.QuestionnaireScreen(
                        messagesAction.questionnaireId
                    )
                )
            }

            is MessagesAction.VideoSectionAction -> {
                viewModelScope.launch {
                    handleVideoSectionAction(messagesAction)
                }
            }

            MessagesAction.NoAction -> logger.i { "No action for message $messageId" }
        }
        if (isDismissible) {
            messageRepository.completeMessage(messageId = messageId)
        }
    }

    private suspend fun handleVideoSectionAction(messageAction: MessagesAction.VideoSectionAction) {
        engageEducationRepository.getVideoBySectionAndVideoId(
            messageAction.videoSectionVideo.videoSectionId,
            messageAction.videoSectionVideo.videoId
        ).getOrNull()?.let { video ->
            navigator.navigateTo(
                EducationNavigationEvent.VideoSectionClicked(
                    video = video
                )
            )
        }
    }

    private fun handleHealthSummaryAction(messageId: String) {
        val setLoading = { loading: Boolean ->
            _uiState.update {
                it.copy(
                    messages = it.messages.map { message ->
                        if (message.id == messageId) {
                            message.copy(isLoading = loading)
                        } else {
                            message
                        }
                    }
                )
            }
        }
        viewModelScope.launch {
            setLoading(true)
            healthSummaryService.generateHealthSummaryPdf()
            setLoading(false)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        bleService.stop()
    }

    private fun handleToggleExpandAction(action: Action.ToggleExpand) {
        _uiState.update {
            it.copy(
                messages = it.messages.map { message ->
                    if (message.id == action.message.id) {
                        message.copy(isExpanded = !message.isExpanded)
                    } else {
                        message
                    }
                }
            )
        }
    }

    private fun handleConfirmMeasurementAction(action: Action.ConfirmMeasurement) {
        _uiState.update {
            it.copy(
                measurementDialog = it.measurementDialog.copy(
                    isProcessing = true,
                )
            )
        }
        viewModelScope.launch {
            measurementsRepository.save(measurement = action.measurement)
            _uiState.update {
                it.copy(
                    measurementDialog = it.measurementDialog.copy(
                        isVisible = false,
                        measurement = null,
                        isProcessing = false,
                    )
                )
            }
        }
    }
}
