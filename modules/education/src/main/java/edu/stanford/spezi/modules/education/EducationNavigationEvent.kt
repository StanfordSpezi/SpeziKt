package edu.stanford.spezi.modules.education

import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.modules.education.videos.Video

sealed class EducationNavigationEvent : NavigationEvent {
    data class VideoSectionClicked(val video: Video) :
        EducationNavigationEvent()

    data object PopUp : EducationNavigationEvent()
}
