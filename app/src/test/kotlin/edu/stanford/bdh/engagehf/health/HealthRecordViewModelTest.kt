package edu.stanford.bdh.engagehf.health

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.spezi.core.testing.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HealthRecordViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private val uiStateMapper: HealthUiStateMapper = mockk(relaxed = true)
    private val healthRepository: HealthRepository = mockk(relaxed = true)
    private val uiData = HealthUiData(
        infoRowData = InfoRowData(
            formattedDate = "",
            formattedValue = "",
            isSelectedTimeRangeDropdownExpanded = false,
            selectedTimeRange = TimeRange.DAILY,
        ),
        valueFormatter = { "" }
    )
    private val mappedUiState = HealthUiState.Success(data = uiData)

    @Before
    fun setup() {
        coEvery {
            healthRepository.observeWeightRecords()
        } returns flowOf(Result.success(emptyList()))
        coEvery {
            healthRepository.observeHeartRateRecords()
        } returns flowOf(Result.success(emptyList()))

        coEvery {
            healthRepository.observeBloodPressureRecords()
        } returns flowOf(Result.success(emptyList()))

        every { uiStateMapper.mapToHealthData(any(), any()) } returns mappedUiState
    }

    @Test
    fun `it should start observing correctly for heart rate`() {
        // given
        val type = RecordType.HEART_RATE

        // when
        val viewModel = createViewModel(type)

        // then
        coEvery { healthRepository.observeHeartRateRecords() }
        assertThat(viewModel.getUiData()).isEqualTo(uiData)
    }

    @Test
    fun `it should handle error correctly`() {
        // given
        coEvery {
            healthRepository.observeBloodPressureRecords()
        } returns flowOf(Result.failure(Error("error")))

        // when
        val viewModel = createViewModel(RecordType.BLOOD_PRESSURE)

        // then
        assertThat(viewModel.uiState.value).isInstanceOf(HealthUiState.Error::class.java)
    }

    @Test
    fun `it should start observing correctly for weight`() {
        // given
        val type = RecordType.WEIGHT

        // when
        val viewModel = createViewModel(type)

        // then
        coEvery { healthRepository.observeWeightRecords() }
        assertThat(viewModel.getUiData()).isEqualTo(uiData)
    }

    @Test
    fun `it should start observing correctly for blood pressure`() {
        // given
        val type = RecordType.BLOOD_PRESSURE

        // when
        val viewModel = createViewModel(type)

        // then
        coEvery { healthRepository.observeBloodPressureRecords() }
        assertThat(viewModel.getUiData()).isEqualTo(uiData)
    }

    @Test
    fun `it should handle DismissConfirmationAlert correctly`() {
        // given
        val action = HealthAction.DismissConfirmationAlert
        val viewModel = createViewModel()

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.getUiData().deleteRecordAlertData).isNull()
    }

    @Test
    fun `it should handle RequestDeleteRecord correctly`() {
        // given
        val data: DeleteRecordAlertData = mockk()
        val action = HealthAction.RequestDeleteRecord("record")
        every { uiStateMapper.mapDeleteRecordAlertData(action) } returns data
        val viewModel = createViewModel()

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.getUiData().deleteRecordAlertData).isEqualTo(data)
    }

    @Test
    fun `it should handle UpdateTimeRange correctly`() {
        // given
        val timeRange = TimeRange.DAILY
        val newUiState: HealthUiState.Success = mockk()
        val action = HealthAction.UpdateTimeRange(timeRange)
        every { uiStateMapper.updateTimeRange(mappedUiState, timeRange) } returns newUiState
        val viewModel = createViewModel()

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value).isEqualTo(newUiState)
    }

    @Test
    fun `it should handle DescriptionBottomSheet correctly`() {
        // given
        RecordType.entries.forEach {
            val expectedEvent = when (it) {
                RecordType.WEIGHT -> AppScreenEvents.Event.WeightDescriptionBottomSheet
                RecordType.BLOOD_PRESSURE -> AppScreenEvents.Event.BloodPressureDescriptionBottomSheet
                RecordType.HEART_RATE -> AppScreenEvents.Event.HeartRateDescriptionBottomSheet
            }
            val viewModel = createViewModel(it)

            // when
            viewModel.onAction(HealthAction.DescriptionBottomSheet)

            // then
            verify { appScreenEvents.emit(expectedEvent) }
        }
    }

    @Test
    fun `it should handle ToggleTimeRangeDropdown correctly`() {
        // given
        val newUiState: HealthUiState.Success = mockk()
        val action = HealthAction.ToggleTimeRangeDropdown(true)
        every { uiStateMapper.mapToggleTimeRange(action, mappedUiState) } returns newUiState
        val viewModel = createViewModel()

        // when
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value).isEqualTo(newUiState)
    }

    @Test
    fun `it should handle DeleteRecord correctly`() {
        // given
        val recordId = "some record id"
        RecordType.entries.forEach {
            val viewModel = createViewModel(it)

            // when
            viewModel.onAction(HealthAction.Async.DeleteRecord(recordId))

            // then
            coEvery {
                when (it) {
                    RecordType.WEIGHT ->
                        healthRepository.deleteWeightRecord(recordId)

                    RecordType.BLOOD_PRESSURE ->
                        healthRepository.deleteBloodPressureRecord(recordId)

                    RecordType.HEART_RATE ->
                        healthRepository.deleteHeartRateRecord(recordId)
                }
            }
            assertThat(viewModel.getUiData().deleteRecordAlertData).isNull()
        }
    }

    private fun HealthRecordViewModel.getUiData() = (uiState.value as HealthUiState.Success).data

    private fun createViewModel(recordType: RecordType = RecordType.WEIGHT) =
        HealthRecordViewModel(
            recordType = recordType,
            appScreenEvents = appScreenEvents,
            uiStateMapper = uiStateMapper,
            healthRepository = healthRepository,
        )
}
