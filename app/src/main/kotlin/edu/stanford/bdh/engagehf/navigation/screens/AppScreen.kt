package edu.stanford.bdh.engagehf.navigation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreen
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.modules.education.videos.EducationScreen

@Composable
fun AppScreen() {
    val viewModel = hiltViewModel<AppScreenViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AppScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun AppScreen(
    uiState: AppUiState,
    onAction: (Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.testIdentifier(AppScreenTestIdentifier.ROOT),
        topBar = {
            AppTopAppBar(
                modifier = Modifier.testIdentifier(identifier = AppScreenTestIdentifier.TOP_APP_BAR),
                title = {
                    Text(
                        text = stringResource(id = uiState.selectedItem.label),
                        modifier = Modifier.testIdentifier(
                            AppScreenTestIdentifier.TOP_APP_BAR_TITLE
                        )
                    )
                })
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 0.5.dp,
                )
                NavigationBar(
                    tonalElevation = (-1).dp,
                ) {
                    uiState.items.forEach { item ->
                        NavigationBarItem(
                            modifier = Modifier.testIdentifier(
                                identifier = AppScreenTestIdentifier.NAVIGATION_BAR_ITEM,
                                suffix = stringResource(id = item.label)
                            ),
                            icon = {
                                Icon(
                                    painter = painterResource(id = if (uiState.selectedItem == item) item.selectedIcon else item.icon),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = item.label)) },
                            selected = uiState.selectedItem == item,
                            onClick = {
                                onAction(Action.UpdateSelectedBottomBarItem(item))
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (uiState.selectedItem) {
                BottomBarItem.HOME -> BluetoothScreen()
                BottomBarItem.HEART_HEALTH -> BluetoothScreen()
                BottomBarItem.MEDICATION -> EducationScreen()
                BottomBarItem.EDUCATION -> EducationScreen()
            }
        }
    }
}

enum class AppScreenTestIdentifier {
    ROOT,
    NAVIGATION_BAR_ITEM,
    TOP_APP_BAR,
    TOP_APP_BAR_TITLE,
}
