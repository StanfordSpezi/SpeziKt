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

        val videoList = videosResult.mapNotNull { videoDocument ->
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

    private fun mapVideo(document: DocumentSnapshot): Video? {
        val videoTitle = getLocalizedString(document, "title") ?: return null
        val videoDescription = getLocalizedString(document, "description")
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
