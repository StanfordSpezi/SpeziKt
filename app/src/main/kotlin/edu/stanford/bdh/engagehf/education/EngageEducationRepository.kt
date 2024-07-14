package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EngageEducationRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val mapper: VideoSectionDocumentToVideoSectionMapper,
) : EducationRepository {
    override suspend fun getVideoSections(): Result<List<VideoSection>> {
        return runCatching {
            firebaseFirestore.collection("videoSections").get().await().mapNotNull { document ->
                mapper.map(document)
            }.sortedBy { it.orderIndex }
        }
    }

    override suspend fun getVideoBySectionAndVideoId(
        sectionId: String,
        videoId: String,
    ): Result<Video> {
        return runCatching {
            firebaseFirestore.collection("videoSections")
                .document(sectionId)
                .collection("videos")
                .document(videoId)
                .get()
                .await().let { document ->
                    if (document.exists()) {
                        println(document.data)
                        mapper.mapVideo(document)
                    } else {
                        throw IllegalStateException("Video not found")
                    }
                } ?: error("Video not found")
        }
    }
}
