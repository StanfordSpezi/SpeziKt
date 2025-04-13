package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PhoneNumberSettingsScreen() {
    val viewModel = hiltViewModel<PhoneNumberSettingsViewModel>()
    val state by viewModel.uiState.collectAsState()
    state.Content()
}
