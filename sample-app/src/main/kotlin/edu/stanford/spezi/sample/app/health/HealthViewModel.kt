package edu.stanford.spezi.sample.app.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.HealthQueryTimeRange
import edu.stanford.spezi.health.RecordType
import edu.stanford.spezi.sample.app.NavigationEvent
import edu.stanford.spezi.sample.app.Navigator
import edu.stanford.spezi.ui.CommonScaffold
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.DisplayedEffect
import edu.stanford.spezi.ui.LoadingLayout
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val navigator: Navigator,
    private val health: Health,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)

    val content = HealthScreenContent(
        title = "Health",
        onBackClicked = { navigator.navigateTo(NavigationEvent.PopBackStack) },
        onDisplayed = ::update,
        state = _uiState.asStateFlow(),
    )

    init {
        viewModelScope.launch {
            health.isFullyAuthorizedState.collect {
                update()
            }
        }
    }

    private fun update() {
        viewModelScope.launch {
            val dataAccessRequirements = health.dataAccessRequirements

            val permissionSection = PermissionsSection(
                title = "Permissions",
                action = if (health.isFullyAuthorizedState.value) {
                    null
                } else {
                    PermissionAction(
                        onClick = {
                            health.requestPermissionsIfNeeded(activity = it)
                        }
                    )
                },
                items = dataAccessRequirements.read.map { recordType ->
                    buildPermissionItem(record = recordType, isRead = true)
                } + dataAccessRequirements.write.map { recordType ->
                    buildPermissionItem(record = recordType, isRead = false)
                }
            )
            val queriesSection = QueriesSection(
                title = "Today's Records",
                queries = dataAccessRequirements.read.map {
                    var totalRecords = 0
                    QueriedRecordItem(
                        title = StringResource(label(it)),
                        description = health.continuousQuery(
                            type = it,
                            interval = 5.seconds,
                            timeRange = HealthQueryTimeRange.today(),
                        ).map { result ->
                            val currentBatch = result.added.size
                            totalRecords += currentBatch
                            StringResource("Received records: (Total: $totalRecords, New: $currentBatch)")
                        }
                    )
                }
            )
            val insertSection = InsertSection(
                title = "Insert sample data",
                description = "Insert a sample steps record and observe it under 'Today's Records' if read permission is granted.",
                enabled = health.isAuthorizedToWrite(RecordType.steps),
                onClick = ::insertSampleStepsRecord,
            )
            _uiState.update {
                HealthUiState.Content(
                    permission = permissionSection,
                    queriesSection = queriesSection,
                    insertSection = insertSection,
                )
            }
        }
    }

    private suspend fun buildPermissionItem(record: AnyRecordType, isRead: Boolean): PermissionItemContent {
        val hasPermission = if (isRead) {
            health.isAuthorizedToRead(record)
        } else {
            health.isAuthorizedToWrite(record)
        }
        val recordName = label(recordType = record)
        return PermissionItemContent(
            title = "$recordName ${if (isRead) "(R)" else "(W)"}",
            status = PermissionStatus(hasPermission),
            action = if (!hasPermission) {
                PermissionAction(
                    onClick = {
                        if (isRead) {
                            health.requestReadPermission(type = record, activity = it)
                        } else {
                            health.requestWritePermission(type = record, activity = it)
                        }
                    }
                )
            } else {
                null
            }
        )
    }

    private fun label(recordType: AnyRecordType): String {
        return recordType.identifier
            .removeSuffix("Record")
            .replace(Regex("(?<!^)([A-Z])"), " $1")
    }

    @Suppress("MagicNumber")
    private fun insertSampleStepsRecord() {
        viewModelScope.launch {
            val steps = StepsRecord(
                count = Random.nextLong(100L, 10000L),
                startTime = Instant.now().minusSeconds(3600L),
                endTime = Instant.now(),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata.manualEntry()
            )
            health.insert(steps)
        }
    }
}

data class HealthScreenContent(
    val title: String,
    val onDisplayed: () -> Unit,
    val onBackClicked: () -> Unit,
    val state: StateFlow<HealthUiState>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        DisplayedEffect(onDisplayed = onDisplayed)
        CommonScaffold(
            title = title,
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                    )
                }
            },
            content = {
                Box(
                    modifier = Modifier.padding(Spacings.medium)
                ) {
                    val uiState = state.collectAsState().value
                    when (uiState) {
                        is HealthUiState.Loading -> LoadingLayout()
                        is HealthUiState.Content -> {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(Spacings.medium)
                            ) {
                                uiState.permission.Content()
                                uiState.queriesSection.Content()
                                uiState.insertSection.Content()
                            }
                        }
                    }
                }
            }
        )
    }
}

sealed interface HealthUiState {
    data object Loading : HealthUiState
    data class Content(
        val permission: PermissionsSection,
        val queriesSection: QueriesSection,
        val insertSection: InsertSection,
    ) : HealthUiState
}
