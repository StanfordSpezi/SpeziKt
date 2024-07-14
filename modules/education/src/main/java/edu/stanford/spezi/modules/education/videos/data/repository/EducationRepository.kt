package edu.stanford.spezi.modules.education.videos.data.repository

import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection

interface EducationRepository {
    suspend fun getVideoSections(): Result<List<VideoSection>>

    suspend fun getVideoBySectionAndVideoId(sectionId: String, videoId: String): Result<Video>
}
