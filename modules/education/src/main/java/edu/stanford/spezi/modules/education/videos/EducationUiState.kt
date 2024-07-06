package edu.stanford.spezi.modules.education.videos

import kotlinx.serialization.Serializable

data class EducationUiState(
    val videoSections: List<VideoSection> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

data class VideoSection(
    val title: String,
    val description: String,
    val orderIndex: Int = 0,
    val videos: List<Video> = emptyList(),
    var isExpanded: Boolean = false,
)

@Serializable
data class Video(
    val title: String,
    val description: String?,
    val orderIndex: Int = 0,
    val youtubeId: String,
)

internal const val VIDEO_SAVE_STATE_PARAM = "video"

sealed interface Action {
    data class VideoSectionClicked(val video: Video) : Action

    data object Retry : Action
}