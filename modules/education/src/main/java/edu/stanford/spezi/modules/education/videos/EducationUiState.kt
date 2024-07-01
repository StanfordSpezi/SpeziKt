package edu.stanford.spezi.modules.education.videos

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

data class Video(
    val title: String? = null,
    val description: String? = null,
    val orderIndex: Int = 0,
    val youtubeId: String? = null,
)

sealed interface Action {
    data class VideoSectionClicked(val youtubeId: String, val title: String) : Action

    data object Retry : Action
}
