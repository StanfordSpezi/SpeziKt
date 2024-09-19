package edu.stanford.bdh.engagehf.medication.data

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.localization.LocalizedMapReader
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class MedicationRecommendationMapperTest {
    private var documentSnapshot: DocumentSnapshot = mockk()
    private var localizedMapReader: LocalizedMapReader = mockk()
    private var mapper: MedicationRecommendationMapper =
        MedicationRecommendationMapper(localizedMapReader)

    @Test
    fun `map - when document has all required fields, returns MedicationRecommendation`() {
        // Given
        val expectedId = "medicationId"
        val expectedTitle = "Medication Title"
        val expectedDescription = "Medication Description"
        val expectedSubtitle = "Medication Subtitle"
        val expectedType = MedicationRecommendationType.NOT_STARTED
        val expectedVideoPath = "/videoSections/1/videos/1"

        val displayInformation = mapOf(
            "title" to expectedTitle,
            "description" to expectedDescription,
            "subtitle" to expectedSubtitle,
            "type" to "notStarted",
            "videoPath" to expectedVideoPath
        )

        val documentData = mapOf(
            "displayInformation" to displayInformation
        )

        every { documentSnapshot.id } returns expectedId
        every { documentSnapshot.data } returns documentData
        every { localizedMapReader.get("title", displayInformation) } returns expectedTitle
        every {
            localizedMapReader.get(
                "description",
                displayInformation
            )
        } returns expectedDescription
        every { localizedMapReader.get("subtitle", displayInformation) } returns expectedSubtitle
        every { localizedMapReader.get("type", displayInformation) } returns "notStarted"
        every { localizedMapReader.get("videoPath", displayInformation) } returns expectedVideoPath

        // When
        val result = mapper.map(documentSnapshot)

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(expectedId)
        assertThat(result?.title).isEqualTo(expectedTitle)
        assertThat(result?.description).isEqualTo(expectedDescription)
        assertThat(result?.subtitle).isEqualTo(expectedSubtitle)
        assertThat(result?.type).isEqualTo(expectedType)
        assertThat(result?.videoPath).isEqualTo(expectedVideoPath)
    }

    @Test
    fun `map - when document is missing required fields, returns null`() {
        // Given
        val documentData = emptyMap<String, Any>()

        every { documentSnapshot.data } returns documentData

        // When
        val result = mapper.map(documentSnapshot)

        // Then
        assertThat(result).isNull()
    }
}
