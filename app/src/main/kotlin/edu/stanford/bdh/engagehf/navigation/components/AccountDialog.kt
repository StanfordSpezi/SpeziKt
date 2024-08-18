package edu.stanford.bdh.engagehf.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.navigation.screens.AccountUiState
import edu.stanford.bdh.engagehf.navigation.screens.Action
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.surface
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.TextStyles.bodyMedium
import edu.stanford.spezi.core.design.theme.TextStyles.headlineMedium
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.theme.lighten

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
                .padding(Spacings.medium)
        ) {
            Column(
                modifier = Modifier.padding(Spacings.medium),
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
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Sizes.Icon.small)
                            )
                        }
                        Text(
                            text = stringResource(R.string.account), style = TextStyles.titleMedium,
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
                        accountUiState.userName?.let {
                            Text(text = accountUiState.userName, style = headlineMedium)
                            VerticalSpacer(height = Spacings.small)
                        }
                        Text(text = accountUiState.email, style = bodyMedium)
                        VerticalSpacer()
                    }
                }

                HorizontalDivider()
                TextButton(
                    onClick = {
                        onAction(Action.ShowHealthSummary)
                    },
                    modifier = Modifier
                        .align(Alignment.Start),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.health_summary),
                            style = bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (accountUiState.isHealthSummaryLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Sizes.Icon.small),
                                color = primary
                            )
                        }
                    }
                }
                HorizontalDivider()
                VerticalSpacer()
                TextButton(
                    onClick = { onAction(Action.SignOut) },
                    modifier = Modifier
                        .align(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(R.string.sign_out),
                        style = bodyMedium,
                        color = Colors.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

class AppTopBarProvider : PreviewParameterProvider<AccountUiState> {
    override val values: Sequence<AccountUiState> = sequenceOf(
        AccountUiState(
            userName = "John Doe",
            initials = "JD",
            email = "john@doe.de",
            isHealthSummaryLoading = false
        ),
        AccountUiState(
            userName = "Jane Smith",
            email = "jane@smith.com",
            isHealthSummaryLoading = true
        ),
        AccountUiState(
            userName = null,
            email = "john@doe.de",
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
