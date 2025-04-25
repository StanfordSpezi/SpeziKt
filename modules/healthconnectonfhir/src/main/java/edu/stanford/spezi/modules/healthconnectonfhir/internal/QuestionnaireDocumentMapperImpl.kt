package edu.stanford.spezi.modules.healthconnectonfhir.internal

import ca.uhn.fhir.parser.IParser
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import edu.stanford.spezi.modules.healthconnectonfhir.QuestionnaireDocumentMapper
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import javax.inject.Inject

internal class QuestionnaireDocumentMapperImpl @Inject constructor(
    private val jsonParser: IParser,
    private val gson: Gson,
) : QuestionnaireDocumentMapper {
    private val mapType by lazy { object : TypeToken<Map<String, Any>>() {}.type }

    override fun map(questionnaireResponse: QuestionnaireResponse): Map<String, Any> {
        val json = jsonParser.encodeResourceToString(questionnaireResponse)
        return gson.fromJson(json, mapType)
    }

    override fun map(questionnaireDocument: DocumentSnapshot): Questionnaire {
        val json = gson.toJson(questionnaireDocument.data)
        return jsonParser.parseResource(Questionnaire::class.java, json)
    }
}
