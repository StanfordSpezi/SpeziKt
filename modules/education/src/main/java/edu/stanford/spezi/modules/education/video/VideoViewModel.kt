package edu.stanford.spezi.modules.education.video

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import javax.inject.Inject

@HiltViewModel
internal class VideoViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    fun onAction(action: Action) {
        when (action) {
            is Action.BackPressed -> {
                navigator.navigateTo(EducationNavigationEvent.PopUp)
            }
        }
    }
}

sealed class Action {
    data object BackPressed : Action()
}
