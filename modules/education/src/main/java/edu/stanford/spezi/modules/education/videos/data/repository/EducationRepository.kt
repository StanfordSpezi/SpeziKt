package edu.stanford.spezi.modules.education.videos.data.repository

import edu.stanford.spezi.modules.education.videos.VideoSection

interface EducationRepository {
    suspend fun getVideoSections(language: String): Result<List<VideoSection>>
}
