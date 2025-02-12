package edu.stanford.bdh.heartbeat.app.home

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.AsyncButton

@Composable
fun HomePage() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HomePage(uiState, viewModel::onAction)
}

@Composable
private fun HomePage(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    if (uiState.showsSignOutAlert) {
        AlertDialog(
            onDismissRequest = {
                onAction(HomeAction.ShowSignOutAlert(false))
            },
            title = {
                Text("Sign Out")
            },
            text = {
                Text("Do you really want to sign out?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onAction(HomeAction.SignOut)
                }) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onAction(HomeAction.ShowSignOutAlert(false))
                }) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            AsyncButton(
                isLoading = uiState.isLoadingSignOut,
                onClick = {
                    onAction(HomeAction.ShowSignOutAlert(true))
                },
            ) {
                Text("Sign Out")
            }
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.padding(padding),
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = {
                it.loadUrl(uiState.url)
            }
        )
    }
}
