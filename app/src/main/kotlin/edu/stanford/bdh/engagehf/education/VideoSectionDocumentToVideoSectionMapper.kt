package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.localization.DocumentLocalizedFieldReader
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VideoSectionDocumentToVideoSectionMapper @Inject constructor(
    private val fieldReaderFactory: DocumentLocalizedFieldReader.Factory,
) {

    suspend fun map(document: DocumentSnapshot): VideoSection? {
        val fieldReader = fieldReaderFactory.create(document)
        val title = fieldReader.get("title")
        val description = fieldReader.get("description")
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
        val fieldReader = fieldReaderFactory.create(document)

        val videoTitle = fieldReader.get("title")
        val youtubeId = document.get("youtubeId") as? String
        return if (videoTitle != null && youtubeId != null) {
            Video(
                title = videoTitle,
                description = fieldReader.get("description"),
                orderIndex = document.get("orderIndex") as? Int ?: 0,
                youtubeId = youtubeId
            )
        } else {
            null
        }
    }
}
