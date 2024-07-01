package edu.stanford.spezi.modules.education

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class EducationNavigationEvent : NavigationEvent {
    data class VideoSectionClicked(val youtubeId: String, val title: String) :
        EducationNavigationEvent()

    data object PopUp : EducationNavigationEvent()
}
