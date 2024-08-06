package edu.stanford.bdh.engagehf.navigation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.DoNewMeasurementBottomSheet
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreen
import edu.stanford.bdh.engagehf.health.HealthScreen
import edu.stanford.bdh.engagehf.health.weight.bottomsheet.AddWeightBottomSheet
import edu.stanford.bdh.engagehf.health.weight.bottomsheet.WeightDescriptionBottomSheet
import edu.stanford.bdh.engagehf.medication.MedicationScreen
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import edu.stanford.spezi.modules.education.videos.EducationScreen
import kotlinx.coroutines.launch

@Composable
fun AppScreen() {
    val viewModel = hiltViewModel<AppScreenViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AppScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    uiState: AppUiState,
    onAction: (Action) -> Unit,
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )

    LaunchedEffect(key1 = uiState.isBottomSheetExpanded) {
        launch {
            if (uiState.isBottomSheetExpanded) {
                bottomSheetScaffoldState.bottomSheetState.expand()
            } else {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
        }
    }

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
        snapshotFlow { bottomSheetScaffoldState.bottomSheetState.currentValue }
            .collect { state ->
                onAction(Action.UpdateBottomSheetState(isExpanded = state == SheetValue.Expanded))
            }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            when (uiState.bottomSheetContent) {
                BottomSheetContent.NEW_MEASUREMENT_RECEIVED -> {
                }

                BottomSheetContent.DO_NEW_MEASUREMENT -> DoNewMeasurementBottomSheet()
                null -> {
                }

                BottomSheetContent.WEIGHT_DESCRIPTION_INFO -> WeightDescriptionBottomSheet()
                BottomSheetContent.ADD_WEIGHT_RECORD -> AddWeightBottomSheet()
            }
        },
        sheetPeekHeight = 0.dp
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
                                label = { Text(text = stringResource(id = item.label), textAlign = TextAlign.Center) },
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
                    BottomBarItem.HEART_HEALTH -> HealthScreen()
                    BottomBarItem.MEDICATION -> MedicationScreen()
                    BottomBarItem.EDUCATION -> EducationScreen()
                }
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
