package edu.stanford.spezi.modules.education

import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.navigation.NavigationEvent

sealed class EducationNavigationEvent : NavigationEvent {
    data class VideoSectionClicked(val video: Video) :
        EducationNavigationEvent()
}
