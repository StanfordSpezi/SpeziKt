package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.bloodpressure.BloodPressurePage
import edu.stanford.bdh.engagehf.health.components.SwipeBox
import edu.stanford.bdh.engagehf.health.heartrate.HeartRatePage
import edu.stanford.bdh.engagehf.health.symptoms.SymptomsPage
import edu.stanford.bdh.engagehf.health.weight.WeightPage
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.secondary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.lighten
import kotlinx.coroutines.launch

@Composable
fun HealthScreen() {
    val viewModel = hiltViewModel<HealthViewModel>()
    HealthScreen(
        onAction = viewModel::onAction,
    )
}

@Composable
fun HealthScreen(
    onAction: (HealthViewModel.Action) -> Unit,
) {
    val tabs = HealthTab.entries.toTypedArray()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                contentColor = onPrimary,
                containerColor = primary,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .height(3.dp)
                            .background(
                                onPrimary,
                                shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                            )
                    )
                },
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = {
                            Text(
                                text = when (tab) {
                                    HealthTab.Symptoms -> "Symptoms"
                                    HealthTab.Weight -> "Weight"
                                    HealthTab.BloodPressure -> "Blood Pressure"
                                    HealthTab.HeartRate -> "Heart Rate"
                                },
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = onPrimary,
                        unselectedContentColor = onPrimary.lighten()
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .padding(Spacings.medium)
            ) { page ->
                when (tabs[page]) {
                    HealthTab.Symptoms -> SymptomsPage()
                    HealthTab.Weight -> WeightPage()
                    HealthTab.BloodPressure -> BloodPressurePage()
                    HealthTab.HeartRate -> HeartRatePage()
                }
            }
        }
        if (tabs[pagerState.currentPage] == HealthTab.Weight) { // TODO add functionality for other tabs
            FloatingActionButton(
                onClick = {
                    when (tabs[pagerState.currentPage]) {
                        HealthTab.Weight -> onAction(HealthViewModel.Action.AddWeightRecord)
                        HealthTab.BloodPressure -> {
                            onAction(HealthViewModel.Action.AddBloodPressureRecord)
                        }

                        HealthTab.HeartRate -> {
                            onAction(HealthViewModel.Action.HeartRateRecord)
                        }

                        else -> {
                            // do nothing
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacings.medium)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Health Record")
            }
        }
    }
}

@Composable
fun HealthTable(
    modifier: Modifier = Modifier,
    healthEntries: List<TableEntryData>,
    onAction: (HealthAction) -> Unit,
) {
    Column( // TODO -> Should be adapted to lazy column; requires adjustment of the whole header and chart on top of the table
        modifier = modifier.fillMaxSize()
    ) {
        healthEntries.forEach { entry ->
            SwipeBox(onDelete = {
                entry.id?.let {
                    onAction(HealthAction.DeleteRecord(it))
                }
            }, content = {
                HealthTableItem(entry)
            })
        }
    }
}

@Composable
fun HealthTableItem(entry: TableEntryData) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.padding(start = Spacings.medium))
        Text(
            text = entry.formattedValues,
            style = TextStyles.headlineMedium.copy(color = primary),
            modifier = Modifier.padding(vertical = 4.dp)
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
            modifier = Modifier
                .width(60.dp)
                .padding(start = Spacings.small)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = entry.formattedDate,
            style = TextStyles.bodyLarge
        )
    }
}

enum class HealthTab {
    Symptoms,
    Weight,
    BloodPressure,
    HeartRate,
}
