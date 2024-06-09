package edu.stanford.spezi.core.navigation

interface ActionProvider {
    fun provideContinueButtonAction(): () -> Unit
}
