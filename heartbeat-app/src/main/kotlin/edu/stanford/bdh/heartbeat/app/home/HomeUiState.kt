package edu.stanford.bdh.heartbeat.app.home

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import edu.stanford.bdh.heartbeat.app.R
import edu.stanford.spezi.modules.design.component.CommonScaffold
import edu.stanford.spezi.modules.design.component.Screen
import edu.stanford.spezi.ui.SpeziThemePreview
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.ThemePreviews

data class HomeUiState(
    val title: StringResource,
    val showAccountButton: Boolean,
    val accountUiState: AccountUiState?,
    val onAction: (HomeAction) -> Unit,
) : Screen {

    @Composable
    override fun Body(modifier: Modifier) {
        CommonScaffold(
            title = title.text(),
            actions = {
                if (showAccountButton) {
                    IconButton(onClick = { onAction(HomeAction.AccountClicked) }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                        )
                    }
                }
            },
            content = {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            onAction(HomeAction.WebViewCreated(this))
                        }
                    },
                )
                accountUiState?.Sheet(Modifier.systemBarsPadding())
            }
        )
    }
}

@ThemePreviews
@Composable
private fun HomeUiStatePreview() {
    val state = HomeUiState(
        title = StringResource(R.string.app_name),
        accountUiState = null,
        showAccountButton = true,
        onAction = {},
    )
    SpeziThemePreview {
        state.body
    }
}
