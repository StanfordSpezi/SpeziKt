package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.module.onboarding.invitation.await
import edu.stanford.spezi.modules.education.Video
import edu.stanford.spezi.modules.education.VideoSection
import javax.inject.Inject

class VideoSectionDocumentToVideoSectionMapper @Inject constructor() {

    suspend fun map(document: DocumentSnapshot, language: String): VideoSection {
        val title = getLocalizedString(document, "title", language)
        val orderIndex = document.getLong("orderIndex")?.toInt() ?: 0
        val description =
            getLocalizedString(document, "description", language)

        val videosResult = document.reference.collection("videos").get().await()

        val videoList = videosResult.map { videoDocument ->
            mapVideo(videoDocument, language)
        }.sortedBy { it.orderIndex }

        return VideoSection(
            title = title,
            description = description,
            orderIndex = orderIndex,
            videos = videoList
        )
    }

    private fun mapVideo(document: DocumentSnapshot, language: String): Video {
        val videoTitle = getLocalizedString(document, "title", language)
        val videoDescription = getLocalizedString(document, "description", language)

        return Video(
            title = videoTitle,
            description = videoDescription,
            orderIndex = document.get("orderIndex") as? Int ?: 0,
            youtubeId = document.get("youtubeId") as? String
        )
    }

    private fun getLocalizedString(
        document: DocumentSnapshot,
        field: String,
        language: String,
    ): String {
        val fieldContent = document.get(field)
        return if (fieldContent is Map<*, *>) {
            fieldContent[language] as? String ?: ""
        } else {
            fieldContent as? String ?: ""
        }
    }
}
