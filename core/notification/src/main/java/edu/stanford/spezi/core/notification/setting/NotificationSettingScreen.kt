package edu.stanford.spezi.core.notification.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import edu.stanford.spezi.core.design.action.PendingActions
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.design.component.AsyncSwitch
import edu.stanford.spezi.core.design.component.CenteredBoxContent
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
internal fun NotificationSettingScreen(
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
                .padding(horizontal = Spacings.medium)
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
                    notificationSettings = uiState.notificationSettings,
                    onAction = onAction,
                    pendingActions = uiState.notificationSettings.pendingActions,
                )
            }
        }
    })
}

@Composable
private fun NotificationOptions(
    notificationSettings: NotificationSettings,
    onAction: (NotificationSettingViewModel.Action) -> Unit,
    pendingActions: PendingActions<NotificationType>,
) {
    LazyColumn(modifier = Modifier.padding(vertical = Spacings.medium)) {
        notificationSettings.groupBySection().forEach { (section, settings) ->
            item {
                NotificationOptionHeadline(
                    text = when (section) {
                        NotificationType.Section.REMINDERS -> stringResource(R.string.reminders)
                        NotificationType.Section.UPDATES -> stringResource(R.string.updates)
                        NotificationType.Section.TRENDS -> stringResource(R.string.trends)
                    }
                )
            }
            items(settings) { (type, value) ->
                NotificationOptionRow(
                    text = when (type) {
                        NotificationType.APPOINTMENT_REMINDERS -> stringResource(R.string.appointment)
                        NotificationType.MEDICATION_UPDATES -> stringResource(R.string.medications)
                        NotificationType.QUESTIONNAIRE_REMINDERS -> stringResource(R.string.survey)
                        NotificationType.RECOMMENDATION_UPDATES -> stringResource(R.string.recommendations)
                        NotificationType.VITALS_REMINDERS -> stringResource(R.string.vital)
                        NotificationType.WEIGHT_ALERTS -> stringResource(R.string.weight_trends)
                    },
                    checked = value,
                    onCheckedChange = {
                        onAction(NotificationSettingViewModel.Action.SwitchChanged(type, it))
                    },
                    isLoading = pendingActions.containsActionValue(
                        actionValue = type
                    )
                )
            }
        }
    }
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
    isLoading: Boolean = false,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyles.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        AsyncSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            isLoading = isLoading
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
                settings = mapOf(
                    NotificationType.APPOINTMENT_REMINDERS to true,
                    NotificationType.MEDICATION_UPDATES to false,
                    NotificationType.QUESTIONNAIRE_REMINDERS to true,
                    NotificationType.RECOMMENDATION_UPDATES to false,
                    NotificationType.VITALS_REMINDERS to true,
                    NotificationType.WEIGHT_ALERTS to false,
                )
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
