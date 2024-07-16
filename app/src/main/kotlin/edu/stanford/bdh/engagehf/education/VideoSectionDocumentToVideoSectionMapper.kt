package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

class VideoSectionDocumentToVideoSectionMapper @Inject constructor() {

    suspend fun map(document: DocumentSnapshot): VideoSection? {
        val title = getLocalizedString(document, "title")
        val description = getLocalizedString(document, "description")
        if (title == null || description == null) return null
        val orderIndex = document.getLong("orderIndex")?.toInt() ?: 0

        val videosResult = document.reference.collection("videos").get().await()

        val videoList = videosResult.documents.mapNotNull { videoDocument ->
            mapVideo(videoDocument)
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

    fun mapVideo(document: DocumentSnapshot): Video? {
        if (document.exists().not()) return null

        val videoTitle = getLocalizedString(document, "title")
        val youtubeId = document.get("youtubeId") as? String
        return if (videoTitle != null && youtubeId != null) {
            Video(
                title = videoTitle,
                description = getLocalizedString(document, "description"),
                orderIndex = document.get("orderIndex") as? Int ?: 0,
                youtubeId = youtubeId
            )
        } else {
            null
        }
    }

    private fun getLocalizedString(
        document: DocumentSnapshot,
        field: String,
    ): String? {
        val fieldContent = document.get(field)
        val language: String = Locale.getDefault().language
        return if (fieldContent is Map<*, *>) {
            fieldContent[language] as? String
        } else {
            fieldContent as? String
        }
    }
}
