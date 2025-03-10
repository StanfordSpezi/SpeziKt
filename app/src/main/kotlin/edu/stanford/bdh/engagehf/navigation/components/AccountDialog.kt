package edu.stanford.bdh.engagehf.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.navigation.screens.AccountUiState
import edu.stanford.bdh.engagehf.navigation.screens.Action
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Colors.onBackground
import edu.stanford.spezi.ui.Colors.onPrimary
import edu.stanford.spezi.ui.Colors.primary
import edu.stanford.spezi.ui.Colors.secondary
import edu.stanford.spezi.ui.Colors.surface
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.TextStyles.bodyMedium
import edu.stanford.spezi.ui.TextStyles.bodySmall
import edu.stanford.spezi.ui.TextStyles.headlineMedium
import edu.stanford.spezi.ui.TextStyles.headlineSmall
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.lighten

@Composable
fun AccountDialog(accountUiState: AccountUiState, onAction: (Action) -> Unit) {
    Dialog(
        onDismissRequest = {
            if (!accountUiState.isHealthSummaryLoading) {
                onAction(Action.ShowAccountDialog(false))
            }
        },
        properties = DialogProperties()
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = surface.lighten(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.small)
        ) {
            Column(
                modifier = Modifier.padding(Spacings.small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        IconButton(
                            enabled = !accountUiState.isHealthSummaryLoading,
                            onClick = { onAction(Action.ShowAccountDialog(false)) },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_dialog_content_description),
                                tint = primary,
                                modifier = Modifier.size(Sizes.Icon.small)
                            )
                        }
                        Text(
                            text = stringResource(R.string.account),
                            style = TextStyles.titleMedium,
                            color = onBackground,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    accountUiState.initials?.let {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(Sizes.Icon.large)
                                .background(primary, shape = CircleShape)
                        ) {
                            Text(
                                text = accountUiState.initials,
                                style = headlineMedium,
                                color = onPrimary,
                            )
                        }
                    }

                    if (accountUiState.initials == null) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(Sizes.Icon.large),
                            tint = primary
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacings.medium))
                    Column {
                        VerticalSpacer()
                        accountUiState.name?.let {
                            Text(
                                text = it,
                                color = onBackground,
                                style = headlineSmall
                            )
                            VerticalSpacer(height = Spacings.small)
                        }
                        Text(
                            text = accountUiState.email,
                            color = secondary,
                            style = bodySmall
                        )
                        VerticalSpacer()
                    }
                }

                HorizontalDivider()
                AsyncAccountItem(
                    title = stringResource(R.string.health_summary),
                    loading = accountUiState.isHealthSummaryLoading,
                    onClick = { onAction(Action.ShowHealthSummary) },
                )
                TextButton(
                    onClick = {
                        onAction(Action.ShowNotificationSettings)
                    },
                    modifier = Modifier
                        .align(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(R.string.notifications),
                        style = bodyMedium,
                    )
                }
                TextButton(
                    onClick = {
                        onAction(Action.ShowContact)
                    },
                    modifier = Modifier
                        .align(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(R.string.contact),
                        style = bodyMedium,
                    )
                }
                HorizontalDivider()
                VerticalSpacer()
                AsyncAccountItem(
                    title = stringResource(R.string.sign_out),
                    color = Colors.error,
                    loading = accountUiState.isSignOutLoading,
                    onClick = { onAction(Action.SignOut) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.AsyncAccountItem(
    title: String,
    color: Color = Color.Unspecified,
    loading: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        enabled = loading.not(),
        modifier = Modifier.align(Alignment.Start),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = bodyMedium,
                color = if (loading) Color.Unspecified else color,
                modifier = Modifier.weight(1f)
            )
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Sizes.Icon.small),
                    color = primary
                )
            }
        }
    }
}

class AppTopBarProvider : PreviewParameterProvider<AccountUiState> {
    override val values: Sequence<AccountUiState> = sequenceOf(
        AccountUiState(
            initials = "JD",
            isHealthSummaryLoading = false,
            name = "John Doe",
            email = "john@doe.de"
        ),
        AccountUiState(
            name = "John Doe",
            email = "",
            isHealthSummaryLoading = true
        ),
        AccountUiState(
            name = null,
            email = "john@doe.de",
            isSignOutLoading = true,
        )
    )
}

@ThemePreviews
@Composable
fun AccountDialogPreview(
    @PreviewParameter(AppTopBarProvider::class) accountUiState: AccountUiState,
) {
    SpeziTheme {
        AccountDialog(
            accountUiState = accountUiState,
            onAction = {}
        )
    }
}
