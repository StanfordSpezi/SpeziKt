package edu.stanford.spezi.sample.app.health

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.viewmodel.speziViewModel

@Composable
fun HealthScreen() {
    val viewModel = speziViewModel<HealthViewModel>()
    viewModel.content.Content()
}
