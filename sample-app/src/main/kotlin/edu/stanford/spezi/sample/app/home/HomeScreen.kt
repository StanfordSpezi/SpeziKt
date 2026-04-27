package edu.stanford.spezi.sample.app.home

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.viewmodel.speziViewModel

@Composable
fun HomeScreen() {
    val viewModel = speziViewModel<HomeViewModel>()
    viewModel.content.Content()
}
