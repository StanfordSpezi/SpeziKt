package edu.stanford.bdh.heartbeat.app.home

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.R
import edu.stanford.spezi.core.design.component.CommonScaffold

@Composable
fun HomePage() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HomePage(uiState, viewModel::onAction)
}

@Composable
private fun HomePage(
    uiState: _HomeUiState,
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

    CommonScaffold(
        title = stringResource(R.string.app_name),
        actions = {
            IconButton(onClick = {/* TODO */}) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account",
                )
            }
        }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
