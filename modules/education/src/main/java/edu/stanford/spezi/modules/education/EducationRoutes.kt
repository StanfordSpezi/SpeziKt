package edu.stanford.spezi.modules.education

import edu.stanford.spezi.modules.education.videos.Video
import kotlinx.serialization.Serializable

@Serializable
sealed class EducationRoutes {

    @Serializable
    data class VideoDetail(val video: Video) : EducationRoutes()
}
