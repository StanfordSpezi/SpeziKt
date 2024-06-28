package edu.stanford.bdh.engagehf.education

import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.module.onboarding.invitation.await
import edu.stanford.spezi.modules.education.EducationRepository
import edu.stanford.spezi.modules.education.VideoSection
import javax.inject.Inject

class EngageEducationRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val mapper: VideoSectionDocumentToVideoSectionMapper,
) : EducationRepository {
    override suspend fun getVideoSections(language: String): Result<List<VideoSection>> {
        return runCatching {
            firebaseFirestore.collection("videoSections").get().await().map { document ->
                mapper.map(document, language)
            }.sortedBy { it.orderIndex }
        }
    }
}
