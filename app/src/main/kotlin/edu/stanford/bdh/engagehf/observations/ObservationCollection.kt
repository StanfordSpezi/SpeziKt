package edu.stanford.bdh.engagehf.observations

import edu.stanford.spezi.modules.healthconnectonfhir.Loinc

enum class ObservationCollection(val collectionName: String, val loinc: Loinc?) {
    HEART_RATE(collectionName = "heartRateObservations", loinc = Loinc.HEART_RATE),
    BODY_WEIGHT(collectionName = "bodyWeightObservations", loinc = Loinc.WEIGHT),
    BLOOD_PRESSURE(collectionName = "bloodPressureObservations", loinc = Loinc.BLOOD_PRESSURE),
    QUESTIONNAIRE(collectionName = "questionnaireResponses", loinc = Loinc.QUESTIONNAIRE),
    SYMPTOMS(collectionName = "symptomScores", null),
}
