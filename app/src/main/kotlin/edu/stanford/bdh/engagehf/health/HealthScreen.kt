package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.bloodpressure.BloodPressurePage
import edu.stanford.bdh.engagehf.health.heartrate.HeartRatePage
import edu.stanford.bdh.engagehf.health.symptoms.SymptomsPage
import edu.stanford.bdh.engagehf.health.weight.WeightPage
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.secondary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.lighten
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@Composable
fun HealthScreen() {
    val viewModel = hiltViewModel<HealthViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun HealthScreen(
    uiState: HealthViewModel.UiState,
    onAction: (HealthViewModel.Action) -> Unit,
) {
    val tabs = uiState.tabs
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedTab = uiState.selectedTab

    LaunchedEffect(key1 = selectedTab) {
        pagerState.animateScrollToPage(uiState.selectedTabIndex)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HealthTabRow(uiState = uiState, onAction = onAction)
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacings.medium)
            ) { _ ->
                when (selectedTab) {
                    HealthTab.Symptoms -> SymptomsPage()
                    HealthTab.Weight -> WeightPage()
                    HealthTab.BloodPressure -> BloodPressurePage()
                    HealthTab.HeartRate -> HeartRatePage()
                }
            }
        }
        AddRecordFloatingIcon(tab = selectedTab, onAction = onAction)
    }
}

@Composable
private fun HealthTabRow(
    uiState: HealthViewModel.UiState,
    onAction: (HealthViewModel.Action) -> Unit,
) {
    val selectedTabIndex = uiState.selectedTabIndex
    TabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = onPrimary,
        containerColor = primary,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp)
                    .background(
                        color = onPrimary,
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
            )
        },
    ) {
        uiState.tabs.forEach { tab ->
            Tab(
                text = {
                    Text(
                        text = when (tab) {
                            HealthTab.Symptoms -> stringResource(R.string.health_tab_title_symptoms)
                            HealthTab.Weight -> stringResource(R.string.health_tab_title_weight)
                            HealthTab.BloodPressure -> stringResource(R.string.health_tab_title_blood_pressure)
                            HealthTab.HeartRate -> stringResource(R.string.health_tab_title_heart_rate)
                        },
                    )
                },
                selected = uiState.selectedTab == tab,
                onClick = {
                    onAction(HealthViewModel.Action.UpdateTab(tab = tab))
                },
                selectedContentColor = onPrimary,
                unselectedContentColor = onPrimary.lighten()
            )
        }
    }
}

@Composable
private fun BoxScope.AddRecordFloatingIcon(
    tab: HealthTab,
    onAction: (HealthViewModel.Action) -> Unit,
) {
    if (tab != HealthTab.Weight) return
    FloatingActionButton(
        onClick = {
            onAction(HealthViewModel.Action.AddRecord(tab = tab))
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(Spacings.medium)
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add $tab Record")
    }
}

@Composable
fun HealthTableItem(entry: TableEntryData) {
    Row(
        modifier = Modifier
            .padding(vertical = Spacings.extraSmall)
            .testIdentifier(
                identifier = HealthPageTestIdentifier.HEALTH_HISTORY_TABLE_ITEM,
                suffix = entry.id
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = entry.formattedValues,
            style = TextStyles.headlineSmall.copy(color = primary),
            modifier = Modifier.padding(vertical = Spacings.extraSmall)
        )
        Text(
            text = entry.formattedTrend,
            style = if (entry.isTrendPositive) {
                TextStyles.bodySmall.copy(color = primary)
            } else {
                TextStyles.bodySmall.copy(
                    color = secondary
                )
            },
            modifier = Modifier.padding(start = Spacings.small)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = entry.formattedDate,
            style = TextStyles.bodyMedium.copy(color = secondary)
        )
    }
}

enum class HealthTab {
    Symptoms,
    Weight,
    BloodPressure,
    HeartRate,
}
