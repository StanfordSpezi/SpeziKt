package edu.stanford.spezi.core.notification.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.design.component.CenteredBoxContent
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.notification.R

@Composable
fun NotificationSettingScreen() {
    val viewModel = hiltViewModel<NotificationSettingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    NotificationSettingScreen(
        onAction = viewModel::onAction,
        uiState = uiState,
    )
}

@Composable
fun NotificationSettingScreen(
    onAction: (NotificationSettingViewModel.Action) -> Unit,
    uiState: NotificationSettingViewModel.UiState,
) {
    Scaffold(topBar = {
        AppTopAppBar(title = {
            Text(
                text = stringResource(R.string.notification_settings),
            )
        }, navigationIcon = {
            IconButton(onClick = {
                onAction(NotificationSettingViewModel.Action.Back)
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = Spacings.medium, end = Spacings.medium)
        ) {
            when (uiState) {
                is NotificationSettingViewModel.UiState.Error -> {
                    CenteredBoxContent {
                        Text(
                            text = uiState.message,
                            style = TextStyles.headlineMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                NotificationSettingViewModel.UiState.Loading -> {
                    CenteredBoxContent {
                        CircularProgressIndicator(color = primary)
                    }
                }

                is NotificationSettingViewModel.UiState.NotificationSettingsLoaded -> NotificationOptions(
                    notificationSettings = uiState.notificationSettings, onAction = onAction
                )
            }
        }
    })
}

@Composable
private fun NotificationOptions(
    notificationSettings: NotificationSettings,
    onAction: (NotificationSettingViewModel.Action) -> Unit,
) {
    VerticalSpacer()
    NotificationOptionHeadline(text = stringResource(R.string.reminders))
    VerticalSpacer()
    NotificationOptionRow(
        text = stringResource(R.string.appointment),
        checked = notificationSettings.receivesAppointmentReminders,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.APPOINTMENT, it
                )
            )
        }
    )
    NotificationOptionRow(
        text = stringResource(R.string.survey),
        checked = notificationSettings.receivesQuestionnaireReminders,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.QUESTIONNAIRE, it
                )
            )
        }
    )
    NotificationOptionRow(
        text = stringResource(R.string.vital),
        checked = notificationSettings.receivesVitalsReminders,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.VITALS, it
                )
            )
        }
    )
    VerticalSpacer()
    NotificationOptionHeadline(text = stringResource(R.string.updates))
    VerticalSpacer()
    NotificationOptionRow(
        text = stringResource(R.string.medications),
        checked = notificationSettings.receivesMedicationUpdates,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.MEDICATION, it
                )
            )
        }
    )
    NotificationOptionRow(
        text = stringResource(R.string.recommendations),
        checked = notificationSettings.receivesRecommendationUpdates,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.RECOMMENDATION, it
                )
            )
        }
    )
    VerticalSpacer()
    NotificationOptionHeadline(
        text = stringResource(R.string.trends),
    )
    VerticalSpacer()
    NotificationOptionRow(
        text = stringResource(R.string.weight_trends),
        checked = notificationSettings.receivesWeightAlerts,
        onCheckedChange = {
            onAction(
                NotificationSettingViewModel.Action.SwitchChanged(
                    NotificationSettingViewModel.SwitchType.WEIGHT, it
                )
            )
        }
    )
}

@Composable
private fun NotificationOptionHeadline(text: String) {
    Text(
        text = text,
        style = TextStyles.headlineMedium,
    )
}

@Composable
private fun NotificationOptionRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyles.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private class NotificationUiStateProvider :
    PreviewParameterProvider<NotificationSettingViewModel.UiState> {
    override val values = sequenceOf(
        NotificationSettingViewModel.UiState.Loading,
        NotificationSettingViewModel.UiState.Error("An error occurred"),
        NotificationSettingViewModel.UiState.NotificationSettingsLoaded(
            notificationSettings = NotificationSettings(
                receivesAppointmentReminders = true,
                receivesQuestionnaireReminders = true,
                receivesVitalsReminders = true,
            )
        )
    )
}

@ThemePreviews
@Composable
private fun NotificationsScreenPreview(
    @PreviewParameter(NotificationUiStateProvider::class) uiState: NotificationSettingViewModel.UiState,
) {
    SpeziTheme {
        NotificationSettingScreen(
            onAction = {}, uiState = uiState
        )
    }
}
