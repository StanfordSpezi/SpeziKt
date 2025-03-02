package edu.stanford.bdh.heartbeat.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.AsyncTextButton
import edu.stanford.spezi.core.design.component.BottomSheetComposableContent
import edu.stanford.spezi.core.design.component.CommonScaffold
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.onBackground
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziThemePreview
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.TextStyles.headlineSmall
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlinx.coroutines.launch

data class AccountUiState(
    val email: String,
    val name: String?,
    val actions: List<AccountActionItem>,
    override val onDismiss: () -> Unit = {},
) : BottomSheetComposableContent {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Body(modifier: Modifier) {
        val sheetState = rememberSheetState()
        val scope = rememberCoroutineScope()
        CommonScaffold(
            title = "Account",
            actions = {
                IconButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = Colors.onPrimary,
                        contentDescription = "Close",
                    )
                }
            },
            content = {
                LazyColumn(
                    modifier = Modifier.padding(Spacings.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacings.medium)
                ) {
                    item { Header() }

                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = Spacings.small)
                        )
                    }

                    items(actions) { it.body }
                }
            }
        )
    }

    @Composable
    private fun Header() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(Sizes.Icon.large),
                tint = primary
            )
            Column {
                name?.let {
                    Text(
                        text = it,
                        color = onBackground,
                        style = headlineSmall
                    )
                }
                Text(
                    text = email,
                    color = onBackground,
                    style = if (name == null) headlineSmall else TextStyles.bodyMedium
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AccountPreview() {
    SpeziThemePreview {
        AccountUiState(
            email = "test.email@test.com",
            name = "Username",
            actions = listOf(
                AccountActionItem(
                    title = "Delete your account",
                    color = { onBackground },
                    confirmation = "",
                    confirmButton = AsyncTextButton(title = "")
                ),
                AccountActionItem(
                    title = "Sign out",
                    color = { Colors.error },
                    confirmation = "",
                    confirmButton = AsyncTextButton(title = "")
                ),
            )
        ) {}.body
    }
}
