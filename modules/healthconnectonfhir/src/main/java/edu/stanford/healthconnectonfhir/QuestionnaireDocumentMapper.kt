package edu.stanford.healthconnectonfhir

import ca.uhn.fhir.parser.IParser
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Questionnaire
import javax.inject.Inject

class QuestionnaireDocumentMapper @Inject constructor(
    private val jsonParser: IParser,
    private val gson: Gson,
) {
    private val mapType by lazy { object : TypeToken<Map<String, Any>>() {}.type }

    fun map(observation: Observation): Map<String, Any> {
        val json = jsonParser.encodeResourceToString(observation)
        return gson.fromJson(json, mapType)
    }

    fun map(questionnaireDocument: DocumentSnapshot): Questionnaire {
        val json = gson.toJson(questionnaireDocument.data)
        return jsonParser.parseResource(Questionnaire::class.java, json)
    }
}
