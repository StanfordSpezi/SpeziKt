package edu.stanford.spezi.modules.education.videos

import edu.stanford.spezi.ui.StringResource
import kotlinx.serialization.Serializable

data class EducationUiState(
    val videoSections: List<VideoSection> = emptyList(),
)

sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: EducationUiState) : UiState
    data class Error(val message: StringResource) : UiState
}

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
    val description: String,
    val orderIndex: Int = 0,
    val youtubeId: String,
) {
    val thumbnailUrl: String
        get() = "https://i3.ytimg.com/vi/$youtubeId/hqdefault.jpg"
}

internal const val VIDEO_SAVE_STATE_PARAM = "video"

sealed interface Action {
    data class VideoSectionClicked(val video: Video) : Action
    data class OnExpand(val videoSection: VideoSection) : Action

    data object Retry : Action
}
