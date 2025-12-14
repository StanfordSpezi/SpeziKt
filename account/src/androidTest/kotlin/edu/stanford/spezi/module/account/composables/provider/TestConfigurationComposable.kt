package edu.stanford.spezi.module.account.composables.provider

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class TestConfigurationIdentifier {
    CONTENT,
}

@Composable
fun TestConfigurationComposable(
    configuration: TestConfiguration,
    content: @Composable () -> Unit,
) {
    TestModule.configuration = configuration
    val isLoaded = remember { mutableStateOf(false) }
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val viewModel = hiltViewModel<TestConfigurationViewModel>()

    LaunchedEffect(Unit) {
        GlobalScope.launch {
            viewState.value = ViewState.Processing
            try {
                viewModel.configure(configuration)
                viewState.value = ViewState.Idle
                isLoaded.value = true
            } catch (throwable: Throwable) {
                viewState.value = ViewState.Error(throwable)
            }
        }
    }

    ViewStateAlert(viewState)

    if (viewState.value == ViewState.Idle && isLoaded.value) {
        val account = viewModel.configuration.account
        CompositionLocalProvider(LocalAccount provides account) {
            Box(Modifier.testIdentifier(TestConfigurationIdentifier.CONTENT)) {
                content()
            }
        }
    }
}
