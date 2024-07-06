package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.module.onboarding.invitation.await
import edu.stanford.spezi.modules.education.videos.VideoSection
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
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
}
