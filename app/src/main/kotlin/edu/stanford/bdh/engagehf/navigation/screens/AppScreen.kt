@file:OptIn(ExperimentalMaterial3Api::class)

package edu.stanford.bdh.engagehf.navigation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.DoNewMeasurementBottomSheet
import edu.stanford.bdh.engagehf.bluetooth.pairing.BLEDevicePairingBottomSheet
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreen
import edu.stanford.bdh.engagehf.health.HealthScreen
import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.AddBloodPressureBottomSheet
import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.BloodPressureDescriptionBottomSheet
import edu.stanford.bdh.engagehf.health.heartrate.bottomsheet.AddHeartRateBottomSheet
import edu.stanford.bdh.engagehf.health.heartrate.bottomsheet.HeartRateDescriptionBottomSheet
import edu.stanford.bdh.engagehf.health.symptoms.SymptomsDescriptionBottomSheet
import edu.stanford.bdh.engagehf.health.weight.bottomsheet.AddWeightBottomSheet
import edu.stanford.bdh.engagehf.health.weight.bottomsheet.WeightDescriptionBottomSheet
import edu.stanford.bdh.engagehf.medication.ui.MedicationScreen
import edu.stanford.bdh.engagehf.navigation.components.AccountTopAppBarButton
import edu.stanford.spezi.core.design.component.AppTopAppBar
import edu.stanford.spezi.core.design.theme.Spacings
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

@Composable
fun AppScreen(
    uiState: AppUiState,
    onAction: (Action) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    LaunchedEffect(key1 = uiState.bottomSheetContent) {
        launch {
            if (uiState.bottomSheetContent != null) {
                bottomSheetState.expand()
            } else {
                bottomSheetState.hide()
            }
        }
    }
    BottomSheetScaffoldContent(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        uiState = uiState,
        onAction = onAction
    )
}

@Composable
fun BottomSheetScaffoldContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    uiState: AppUiState,
    onAction: (Action) -> Unit,
) {
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(
                uiState = uiState,
                onAction = onAction,
                sheetState = bottomSheetScaffoldState.bottomSheetState,
            )
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            modifier = Modifier.testIdentifier(AppScreenTestIdentifier.ROOT),
            topBar = {
                AppTopAppBar(
                    modifier = Modifier.testIdentifier(identifier = AppScreenTestIdentifier.TOP_APP_BAR),
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = Spacings.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = uiState.selectedItem.label),
                                modifier = Modifier.testIdentifier(
                                    AppScreenTestIdentifier.TOP_APP_BAR_TITLE
                                )
                            )
                        }
                    },
                    actions = {
                        AccountTopAppBarButton(uiState.accountUiState, onAction = onAction)
                    }
                )
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
                                        painter = painterResource(
                                            id = if (uiState.selectedItem == item) {
                                                item.selectedIcon
                                            } else {
                                                item.icon
                                            }
                                        ),
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(id = item.label),
                                        textAlign = TextAlign.Center
                                    )
                                },
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

@Composable
private fun BottomSheetContent(
    uiState: AppUiState,
    onAction: (Action) -> Unit,
    sheetState: SheetState,
) {
    LaunchedEffect(Unit) {
        var lastValue = sheetState.currentValue
        snapshotFlow { sheetState.currentValue }
            .collect {
                val movingToPartiallyExpanded = lastValue == SheetValue.Expanded &&
                    it == SheetValue.PartiallyExpanded
                if (movingToPartiallyExpanded || it == SheetValue.Hidden) {
                    onAction(Action.DismissBottomSheet)
                }
                lastValue = it
            }
    }
    when (uiState.bottomSheetContent) {
        BottomSheetContent.DO_NEW_MEASUREMENT -> DoNewMeasurementBottomSheet()
        BottomSheetContent.WEIGHT_DESCRIPTION_INFO -> WeightDescriptionBottomSheet()
        BottomSheetContent.ADD_WEIGHT_RECORD -> AddWeightBottomSheet()
        BottomSheetContent.NEW_MEASUREMENT_RECEIVED, null -> {}
        BottomSheetContent.ADD_BLOOD_PRESSURE_RECORD -> AddBloodPressureBottomSheet()
        BottomSheetContent.ADD_HEART_RATE_RECORD -> AddHeartRateBottomSheet()
        BottomSheetContent.BLOOD_PRESSURE_DESCRIPTION_INFO -> BloodPressureDescriptionBottomSheet()
        BottomSheetContent.HEART_RATE_DESCRIPTION_INFO -> HeartRateDescriptionBottomSheet()
        BottomSheetContent.BLUETOOTH_DEVICE_PAIRING -> BLEDevicePairingBottomSheet()
        BottomSheetContent.SYMPTOMS_DESCRIPTION_INFO -> SymptomsDescriptionBottomSheet()
    }
}

enum class AppScreenTestIdentifier {
    ROOT,
    NAVIGATION_BAR_ITEM,
    TOP_APP_BAR,
    TOP_APP_BAR_TITLE,
}
