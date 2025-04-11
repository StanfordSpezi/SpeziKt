package edu.stanford.bdh.engagehf.new_onboarding

import androidx.compose.runtime.Composable

class OnboardingFlow(private val screens: List<OnboardingView>) {

    init {
        for (i in 0..screens.size - 1) {
            screens[i].setNext(screens[i+1])
        }
    }

    @Composable
    fun Content() {

    }
}