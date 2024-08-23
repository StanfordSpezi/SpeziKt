package edu.stanford.healthconnectonfhir

import ca.uhn.fhir.parser.IParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionnaireDocumentMapperTest {

    private var jsonParser: IParser = mockk()
    private var gson: Gson = mockk()
    private var mapper: QuestionnaireDocumentMapper = QuestionnaireDocumentMapper(jsonParser, gson)

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
}
