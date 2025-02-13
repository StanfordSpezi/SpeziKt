package edu.stanford.bdh.heartbeat.app.home

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.AsyncButton
import edu.stanford.spezi.core.design.theme.Spacings

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
    if (uiState.showsDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(HomeAction.ShowDeleteDialog(false))
            },
            title = {
                Text("Delete Account")
            },
            text = {
                Text("Do you really want to delete your account? This action cannot be reversed.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(HomeAction.Delete)
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(HomeAction.ShowDeleteDialog(false))
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
    if (uiState.showsSignOutDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(HomeAction.ShowSignOutDialog(false))
            },
            title = {
                Text("Sign Out")
            },
            text = {
                Text("Do you really want to sign out?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(HomeAction.SignOut)
                    },
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(HomeAction.ShowSignOutDialog(false))
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacings.medium, Alignment.CenterHorizontally),
                modifier = Modifier.padding(horizontal = Spacings.medium, vertical = Spacings.small).fillMaxWidth()
            ) {
                AsyncButton(
                    isLoading = uiState.isLoadingSignOut,
                    onClick = {
                        onAction(HomeAction.ShowSignOutDialog(true))
                    },
                ) {
                    Text("Sign Out")
                }

                AsyncButton(
                    isLoading = uiState.isLoadingDelete,
                    onClick = {
                        onAction(HomeAction.ShowDeleteDialog(true))
                    },
                ) {
                    Text("Delete")
                }
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
