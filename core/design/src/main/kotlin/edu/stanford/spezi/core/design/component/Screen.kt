package edu.stanford.spezi.core.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

interface Screen : ComposableContent

data class ScreenBuilder(
    private val builder: @Composable (modifier: Modifier) -> Unit
) : Screen {
    @Composable
    override fun Body(modifier: Modifier) {
        builder.invoke(modifier)
    }
}

abstract class ScreenViewModel : ViewModel() {
    abstract val screen: Screen
}

@Composable
inline fun <reified VM : ScreenViewModel> Screen(modifier: Modifier = Modifier) {
    hiltViewModel<VM>().screen.Body(modifier)
}