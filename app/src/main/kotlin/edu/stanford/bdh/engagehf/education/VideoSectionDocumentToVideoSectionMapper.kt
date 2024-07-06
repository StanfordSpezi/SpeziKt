package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.module.onboarding.invitation.await
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import java.util.Locale
import javax.inject.Inject

class VideoSectionDocumentToVideoSectionMapper @Inject constructor() {

    suspend fun map(document: DocumentSnapshot): VideoSection? {
        val currentLocale: Locale = Locale.getDefault()
        val language: String = currentLocale.language
        val title = getLocalizedString(document, "title", language) ?: return null
        val orderIndex = document.getLong("orderIndex")?.toInt() ?: 0
        val description =
            getLocalizedString(document, "description", language) ?: return null

        val videosResult = document.reference.collection("videos").get().await()

        val videoList = videosResult.mapNotNull { videoDocument ->
            mapVideo(videoDocument, language)
        }.sortedBy { it.orderIndex }

        if (videoList.isEmpty()) {
            return null
        }

        return VideoSection(
            title = title,
            description = description,
            orderIndex = orderIndex,
            videos = videoList
        )
    }

    private fun mapVideo(document: DocumentSnapshot, language: String): Video? {
        val videoTitle = getLocalizedString(document, "title", language) ?: return null
        val videoDescription = getLocalizedString(document, "description", language)
        val youtubeId = document.get("youtubeId") as? String ?: return null

        return Video(
            title = videoTitle,
            description = videoDescription,
            orderIndex = document.get("orderIndex") as? Int ?: 0,
            youtubeId = youtubeId
        )
    }

    private fun getLocalizedString(
        document: DocumentSnapshot,
        field: String,
        language: String,
    ): String? {
        val fieldContent = document.get(field)
        return if (fieldContent is Map<*, *>) {
            fieldContent[language] as? String
        } else {
            fieldContent as? String
        }
    }
}
