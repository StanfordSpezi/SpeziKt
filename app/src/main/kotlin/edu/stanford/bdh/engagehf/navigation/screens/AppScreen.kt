package edu.stanford.bdh.engagehf.navigation.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.Action
import edu.stanford.bdh.engagehf.MainActivityViewModel
import edu.stanford.bdh.engagehf.NavigationItemEnum
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreen
import edu.stanford.bdh.engagehf.navigation.data.models.AppUiState
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.modules.education.videos.EducationScreen

@Composable
fun AppScreen() {
    val viewModel = hiltViewModel<MainActivityViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AppScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppScreen(
    uiState: AppUiState,
    onAction: (Action) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopAppBar(title = uiState.navigationItems[uiState.selectedIndex].label)
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 0.5.dp,
                )
                NavigationBar(
                    tonalElevation = (-1).dp,
                ) {
                    uiState.navigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = item.label) },
                            selected = item.selected,
                            onClick = {
                                onAction(Action.UpdateSelectedIndex(index))
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
            when (uiState.navigationItems[uiState.selectedIndex].navigationItem) {
                NavigationItemEnum.Home -> {
                    BluetoothScreen()
                }

                NavigationItemEnum.HeartHealth -> {
                    BluetoothScreen()
                }

                NavigationItemEnum.Medication -> {
                    EducationScreen()
                }

                NavigationItemEnum.Education -> {
                    EducationScreen()
                }
            }
        }
    }
}
