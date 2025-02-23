package edu.stanford.spezi.core.design.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlinx.coroutines.flow.StateFlow

fun interface Screen : ComposableContent

abstract class ScreenViewModel : ViewModel() {
    abstract val screenState: StateFlow<Screen>
}

fun buildScreen(
    builder: @Composable (modifier: Modifier) -> Unit,
): Screen = Screen { modifier -> builder(modifier) }

fun screen(
    builder: ComposeValue<Screen>,
): Screen = Screen { modifier -> builder.invoke().Body(modifier) }

@Composable
inline fun <reified VM : ScreenViewModel> Screen(modifier: Modifier = Modifier) {
    hiltViewModel<VM>().screenState.collectAsState().value.Body(modifier)
}

@Composable
inline fun <reified VM : ScreenViewModel, reified VMF> Screen(
    modifier: Modifier = Modifier,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String?,
    noinline creationCallback: (VMF) -> VM,
) {
    hiltViewModel<VM, VMF>(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        creationCallback = creationCallback,
    ).screenState.collectAsState().value.Body(modifier)
}
