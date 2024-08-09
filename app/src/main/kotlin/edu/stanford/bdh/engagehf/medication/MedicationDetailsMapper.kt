package edu.stanford.bdh.engagehf.medication

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.localization.LocalizedMapReader
import edu.stanford.spezi.core.utils.JsonMap
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MedicationDetailsMapper @Inject constructor(
    private val localizedMapReader: LocalizedMapReader,
) {

    fun map(documentSnapshot: DocumentSnapshot): MedicationDetails? {
        val displayInformation = documentSnapshot.data?.get("displayInformation") as? JsonMap ?: return null
        val title = localizedMapReader.get("title", displayInformation)
        val description = localizedMapReader.get("description", displayInformation)
        val subtitle = localizedMapReader.get("subtitle", displayInformation)
        val type = MedicationRecommendationType.from(localizedMapReader.get("type", displayInformation))
        return if (title != null && description != null && subtitle != null) {
            MedicationDetails(
                id = documentSnapshot.id,
                title = title,
                description = description,
                subtitle = subtitle,
                type = type,
                dosageInformation = getDosageInformation(displayInformation),
            )
        } else {
            null
        }
    }

    private fun getDosageInformation(jsonMap: JsonMap?): DosageInformation? {
        val dosageInformationMap = jsonMap?.get("dosageInformation") as? JsonMap
        val unit = dosageInformationMap?.get("unit") as? String
        val currentScheduleMap = getDosage(key = "currentSchedule", jsonMap = dosageInformationMap)
        val minimumScheduleMap = getDosage(key = "minimumSchedule", jsonMap = dosageInformationMap)
        val targetSchedule = getDosage(key = "targetSchedule", jsonMap = dosageInformationMap)
        return if (unit != null) {
            DosageInformation(
                currentSchedule = currentScheduleMap,
                minimumSchedule = minimumScheduleMap,
                targetSchedule = targetSchedule,
                unit = unit,
            )
        } else {
            null
        }
    }

    private fun getDosage(key: String, jsonMap: JsonMap?): List<DoseSchedule> {
        val dosagesMap = jsonMap?.get(key) as? List<JsonMap>
        return dosagesMap?.map { currentMap ->
            DoseSchedule(
                frequency = (currentMap["frequency"] as? Number)?.toDouble() ?: 0.0,
                dosage = currentMap["quantity"] as? List<Double> ?: emptyList()
            )
        } ?: emptyList()
    }
}
