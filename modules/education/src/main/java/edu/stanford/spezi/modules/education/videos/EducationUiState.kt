package edu.stanford.spezi.modules.education.videos

import kotlinx.serialization.Serializable

data class EducationUiState(
    val videoSections: List<VideoSection> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

data class VideoSection(
    val title: String? = null,
    val description: String? = null,
    val orderIndex: Int = 0,
    val videos: List<Video> = emptyList(),
    var isExpanded: Boolean = false,
)

@Serializable
data class Video(
    val title: String? = null,
    val description: String? = null,
    val orderIndex: Int = 0,
    val youtubeId: String? = null,
)

sealed interface Action {
    data class VideoSectionClicked(val video: Video) : Action

    data object Retry : Action
}
