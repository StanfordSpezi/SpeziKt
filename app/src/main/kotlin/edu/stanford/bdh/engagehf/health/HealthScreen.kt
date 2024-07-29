package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.SwipeBox
import edu.stanford.bdh.engagehf.health.components.TimeRangeDropdown
import edu.stanford.bdh.engagehf.health.weight.WeightData
import edu.stanford.bdh.engagehf.health.weight.WeightPage
import edu.stanford.bdh.engagehf.health.weight.WeightUiData
import edu.stanford.bdh.engagehf.health.weight.WeightViewModel
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.secondary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.lighten
import kotlinx.coroutines.launch

@Composable
fun HealthScreen() {
    val viewModel = hiltViewModel<HealthViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthScreen(
        onAction = viewModel::onAction,
        uiState = uiState
    )
}

@Composable
fun HealthScreen(
    onAction: (HealthViewModel.Action) -> Unit,
    uiState: HealthUiState,
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
                        unselectedContentColor = onPrimary.lighten(isSystemInDarkTheme())
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
                    HealthTab.Symptoms -> Text("Symptoms")
                    HealthTab.Weight -> WeightPage()
                    HealthTab.BloodPressure -> Text("Blood Pressure")
                    HealthTab.HeartRate -> Text(text = "Heart Rate")
                }
            }
        }
        FloatingActionButton(
            onClick = { onAction(HealthViewModel.Action.AddHealthRecord) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacings.medium)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Health Record")
        }
    }
}

@Composable
fun WeightHeader(uiState: WeightUiData, onAction: (WeightViewModel.Action) -> Unit) {
    if (uiState.newestWeight != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = Spacings.medium)
        ) {
            Column {
                Text(
                    text = uiState.newestWeight.formattedValue,
                    style = TextStyles.headlineLarge.copy(color = primary),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = uiState.newestWeight.formattedDate,
                    style = TextStyles.bodyMedium
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TimeRangeDropdown(uiState, onAction)
            IconButton(
                modifier = Modifier.size(Sizes.Icon.large),
                onClick = { onAction(WeightViewModel.Action.WeightDescriptionBottomSheet) }) {
                Icon(
                    painter = painterResource(id = edu.stanford.spezi.core.design.R.drawable.ic_info),
                    contentDescription = stringResource(R.string.weight_icon_content_description),
                    modifier = Modifier
                        .size(Sizes.Icon.medium)
                        .background(primary, shape = CircleShape)
                        .shadow(Spacings.small, CircleShape)
                        .padding(Spacings.small),
                    tint = onPrimary
                )
            }
        }
    }
}

@Composable
fun WeightList(weights: List<WeightData>, onAction: (WeightViewModel.Action) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        weights.forEach { entry ->
            SwipeBox(onDelete = {
                println("delete")
                entry.id?.let {
                    onAction(WeightViewModel.Action.DeleteWeightRecord(it))
                }
            }, content = {
                WeightListItem(entry)
            })
        }
    }
}

// TODO elemente löschen
// TODO trend mit datum wechseln

@Composable
fun WeightListItem(entry: WeightData) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.padding(start = Spacings.medium))
        Text(
            text = entry.formattedValue,
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

@Preview
@Composable
fun HealthScreenPreview() {
    SpeziTheme {
        val healthUiState = HealthUiState()
        HealthScreen(
            onAction = {},
            uiState = healthUiState,
        )
    }
}

enum class HealthTab {
    Symptoms,
    Weight,
    BloodPressure,
    HeartRate,
}
