package edu.stanford.spezi.sample.app.health

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HealthScreen() {
    val viewModel = hiltViewModel<HealthViewModel>()
    viewModel.content.Content()
}
