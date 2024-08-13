package edu.stanford.bdh.engagehf.medication.ui

import android.content.Context
import com.google.common.truth.Truth.assertThat
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
    fun `given medication details when mapMedicationUiState then return sorted success state`() {
        // given
        val recommendations = getRecommendations()

        // when
        val result = medicationUiStateMapper.mapMedicationUiState(recommendations)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).uiModels).hasSize(2)
        assertThat(result.uiModels[0].id).isEqualTo("1")
        assertThat(result.uiModels[1].id).isEqualTo("2")
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
            uiModels = listOf(model)
        )

        // when
        val result = medicationUiStateMapper.expandMedication(medicationId, uiState)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).uiModels).hasSize(1)
        assertThat(result.uiModels.first().isExpanded).isEqualTo(initialIsExpandedState.not())
    }

    private fun getMedicationCardUiModel(
        id: String = "",
        title: String = "",
        subtitle: String = "",
        description: String = "",
        isExpanded: Boolean = false,
        statusIconResId: Int? = null,
        statusColor: MedicationColor = MedicationColor.GREY,
        dosageInformation: DosageInformationUiModel? = null,
    ) = MedicationCardUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        isExpanded = isExpanded,
        statusIconResId = statusIconResId,
        statusColor = statusColor,
        dosageInformation = dosageInformation,
    )

    private fun getRecommendations() = listOf(
        MedicationRecommendation(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null
        ),
        MedicationRecommendation(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED, // higher priority than NOT_STARTED
            dosageInformation = null
        )
    )
}
