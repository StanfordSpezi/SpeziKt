package edu.stanford.spezi.modules.healthconnectonfhir.internal

import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Type

class QuestionnaireDocumentMapperTest {

    private var jsonParser: IParser = mockk()
    private var gson: Gson = mockk()
    private val mapper = QuestionnaireDocumentMapperImpl(jsonParser, gson)

    @Test
    fun `test map DocumentSnapshot to Questionnaire`() {
        // given
        val documentSnapshot = mockk<DocumentSnapshot>()
        val data = mapOf("resourceType" to "Questionnaire")
        val json = """{"resourceType":"Questionnaire"}"""
        val expectedQuestionnaire = mockk<Questionnaire>()

        every { documentSnapshot.data } returns data
        every { gson.toJson(data) } returns json
        every {
            jsonParser.parseResource(
                Questionnaire::class.java,
                json
            )
        } returns expectedQuestionnaire

        // when
        val result = mapper.map(documentSnapshot)

        // then
        assertEquals(expectedQuestionnaire, result)
    }

    @Test
    fun `test map Questionnaire to Map response`() {
        // given
        val json = "some-json"
        val response: QuestionnaireResponse = mockk()
        every { jsonParser.encodeResourceToString(response) } returns json
        val returnValue: Map<String, Any> = mockk()
        every { gson.fromJson<Map<String, Any>>(json, any<Type>()) } returns returnValue

        // when
        val result = mapper.map(response)

        // then
        assertThat(result).isEqualTo(returnValue)
    }
}
