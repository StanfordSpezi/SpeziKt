package edu.stanford.bdh.engagehf.medication.ui

import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.medication.data.DosageInformation
import edu.stanford.bdh.engagehf.medication.data.DoseSchedule
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendation
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendationType
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class MedicationUiStateMapperTest {
    private val context: Context = mockk()

    private var medicationUiStateMapper: MedicationUiStateMapper = MedicationUiStateMapper(
        context = context
    )

    @Before
    fun setup() {
        every { context.getString(any()) } returns "some-string"
    }

    @Test
    fun `given medication details when mapMedicationUiState then return filtered success state`() {
        // given
        val recommendations = getRecommendations()

        // when
        val result = medicationUiStateMapper.mapMedicationUiState(recommendations)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationsTaking.medications).hasSize(1)
        assertThat(result.medicationsTaking.medications[0].id).isEqualTo("1")
    }

    @Test
    fun `given error state and expand action when expandMedication then return error state`() {
        // given
        val uiState = MedicationUiState.Error(message = "An error occurred")

        // when
        val result = medicationUiStateMapper.expandMedication("some-id", uiState)

        // then
        assertThat(result).isEqualTo(uiState)
    }

    @Test
    fun `given success state and expand action when expandMedication then return updated success state`() {
        // given
        val initialIsExpandedState = false
        val medicationId = "id"
        val model = getMedicationCardUiModel(id = medicationId, isExpanded = initialIsExpandedState)
        val uiState = MedicationUiState.Success(
            medicationsTaking = Medications(listOf(model), true),
            medicationsThatMayHelp = Medications(listOf(model), true),
            colorKeyExpanded = true
        )

        // when
        val result = medicationUiStateMapper.expandMedication(medicationId, uiState)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationsTaking.medications).hasSize(1)
        assertThat(result.medicationsTaking.medications.first().isExpanded).isEqualTo(
            initialIsExpandedState.not()
        )
    }

    @Test
    fun `given success state and expand action with non-existent id when expandMedication then return unchanged success state`() {
        // given
        val initialIsExpandedState = false
        val medicationId = "id"
        val model = getMedicationCardUiModel(id = medicationId, isExpanded = initialIsExpandedState)
        val uiState = MedicationUiState.Success(
            medicationsTaking = Medications(listOf(model), true),
            medicationsThatMayHelp = Medications(listOf(model), true),
            colorKeyExpanded = true
        )

        // when
        val result = medicationUiStateMapper.expandMedication("non-existent-id", uiState)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationsTaking.medications).hasSize(1)
        assertThat(result.medicationsTaking.medications.first().isExpanded).isEqualTo(
            initialIsExpandedState
        )
    }

    @Test
    fun `given empty recommendations when mapMedicationUiState then return no data state`() {
        // given
        val recommendations = emptyList<MedicationRecommendation>()

        // when
        val result = medicationUiStateMapper.mapMedicationUiState(recommendations)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.NoData::class.java)
        assertThat((result as MedicationUiState.NoData).message).isEqualTo("some-string")
    }

    @Test
    fun `given recommendations with dosage information when mapMedicationUiState then return success state with dosage information`() {
        // given
        val recommendations = getRecommendationsWithDosage()

        // when
        val result = medicationUiStateMapper.mapMedicationUiState(recommendations)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationsTaking.medications).hasSize(1)
        assertThat(result.medicationsTaking.medications[0].dosageInformation).isNotNull()
    }

    private fun getMedicationCardUiModel(
        id: String = "",
        title: String = "",
        subtitle: String = "",
        description: String = "",
        isExpanded: Boolean = false,
        statusIconResId: Int? = null,
        statusColor: MedicationColor = MedicationColor.BLUE,
        dosageInformation: DosageInformationUiModel? = null,
        videoPath: String = "",
    ) = MedicationCardUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        isExpanded = isExpanded,
        statusIconResId = statusIconResId,
        statusColor = statusColor,
        dosageInformation = dosageInformation,
        videoPath = videoPath
    )

    private fun getRecommendations() = listOf(
        MedicationRecommendation(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null,
            videoPath = null
        ),
        MedicationRecommendation(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED, // higher priority than NOT_STARTED
            dosageInformation = null,
            videoPath = "/videoSections/1/videos/1"
        )
    )

    private fun getRecommendationsWithDosage() = listOf(
        MedicationRecommendation(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED,
            dosageInformation = DosageInformation(
                unit = "mg",
                currentSchedule = listOf(
                    DoseSchedule(
                        frequency = 2.0,
                        quantity = listOf(20.0)
                    )
                ),
                targetSchedule = listOf(
                    DoseSchedule(
                        frequency = 2.0,
                        quantity = listOf(20.0)
                    )
                ),
            ),
            videoPath = "/videoSections/1/videos/1"
        )
    )
}
