package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.WeightRecord
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Quantity
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.Date

class ObservationsDocumentMapperImplTest {
    private var jsonParser: IParser = mockk()
    private var gson: Gson = mockk(relaxed = true)
    private var mapper: ObservationsDocumentMapperImpl =
        ObservationsDocumentMapperImpl(jsonParser, gson)

    @Test
    fun `given observation should map to WeightRecord correctly`() {
        // Given
        val givenId = "123456"
        val givenWeight = 70.5
        val givenDate = Date()
        val observation: Observation = createSampleWeightObservation(
            id = givenId,
            weight = givenWeight,
            date = givenDate
        )

        val documentSnapshot: DocumentSnapshot = mockk(relaxed = true)

        every { documentSnapshot.id } returns "id"
        every { jsonParser.parseResource(Observation::class.java, anyString()) } returns observation

        // When
        val result = mapper.map<WeightRecord>(documentSnapshot)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.metadata.clientRecordId).isEqualTo("id")
        assertThat(result.time).isEqualTo(givenDate.toInstant())
        assertThat(result.weight.inKilograms).isEqualTo(givenWeight)
    }

    private fun createSampleWeightObservation(
        id: String = "123456",
        weight: Double = 70.5,
        date: Date = Date(),
    ): Observation {
        return Observation().apply {
            identifier = listOf(
                Identifier().apply {
                    this.value = id
                }
            )

            category = listOf(
                CodeableConcept().apply {
                    addCoding(
                        Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                            .setCode("vital-signs")
                            .setDisplay("Vital Signs")
                    )
                }
            )

            code = CodeableConcept().apply {
                addCoding(
                    Coding()
                        .setSystem("http://loinc.org")
                        .setCode("29463-7")
                        .setDisplay("Body weight")
                )
                addCoding(
                    Coding()
                        .setSystem("http://health.google/health-connect-android")
                        .setCode("WeightRecord")
                        .setDisplay("Weight Record")
                )
            }

            effective = DateTimeType().apply {
                this.value = date
            }

            value = Quantity().apply {
                this.value = weight.toBigDecimal()
                this.unit = "kg"
                this.system = "http://unitsofmeasure.org"
                this.code = "kg"
            }
        }
    }
}
