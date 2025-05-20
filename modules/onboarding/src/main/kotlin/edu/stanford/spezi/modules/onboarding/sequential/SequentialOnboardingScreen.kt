package edu.stanford.spezi.modules.onboarding.sequential

import android.app.Activity
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.modules.onboarding.sequential.components.OnboardingViewPage
import edu.stanford.spezi.modules.onboarding.sequential.components.PageIndicator
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.ui.theme.Colors.onPrimary
import edu.stanford.spezi.ui.theme.Colors.onSecondary
import edu.stanford.spezi.ui.theme.Colors.onTertiary
import edu.stanford.spezi.ui.theme.Colors.primary
import edu.stanford.spezi.ui.theme.Colors.secondary
import edu.stanford.spezi.ui.theme.Colors.tertiary
import edu.stanford.spezi.ui.theme.Spacings

/**
 * The screen that displays the sequential onboarding steps.
 *
 */
@Composable
fun SequentialOnboardingScreen() {
    val viewModel: SequentialOnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    SequentialOnboardingScreen(
        onAction = viewModel::onAction,
        uiState = uiState,
    )
}

@Composable
fun SequentialOnboardingScreen(
    uiState: SequentialOnboardingUiState,
    onAction: (Action) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val window: Window? = activity?.window

    val state = rememberPagerState(
        pageCount = { uiState.pageCount },
        initialPage = uiState.currentPage
    )

    LaunchedEffect(uiState.currentPage) {
        if (uiState.currentPage != state.currentPage) {
            state.animateScrollToPage(
                page = uiState.currentPage,
                animationSpec = spring(dampingRatio = 0.7f)
            )
        }
    }

    LaunchedEffect(state.currentPage) {
        onAction(Action.SetPage(state.currentPage))
    }

    val colors = listOf(primary, secondary, tertiary)
    val onColors = listOf(onPrimary, onSecondary, onTertiary)
    val currentPageColor = colors[uiState.currentPage % colors.size]
    val currentOnColor = onColors[uiState.currentPage % onColors.size]

    SideEffect {
        window?.statusBarColor = currentPageColor.toArgb()
        window?.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }

    DisposableEffect(key1 = state.currentPage) {
        onDispose {
            window?.statusBarColor = colors[0].toArgb()
        }
    }

    Column(
        modifier = Modifier
            .testIdentifier(SequentialOnboardingScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .background(currentPageColor)
    ) {
        HorizontalPager(
            state,
            modifier = Modifier
                .testIdentifier(SequentialOnboardingScreenTestIdentifier.PAGER)
                .weight(1f)
        ) { index ->
            val step = uiState.steps[index]
            OnboardingViewPage(
                modifier = Modifier
                    .testIdentifier(
                        identifier = SequentialOnboardingScreenTestIdentifier.PAGE,
                        suffix = step.title
                    ),
                backgroundColor = currentPageColor,
                onColor = currentOnColor,
                title = step.title,
                description = step.description,
                iconId = step.icon,
            )
        }
        PageIndicator(
            modifier = Modifier.testIdentifier(SequentialOnboardingScreenTestIdentifier.PAGE_INDICATOR),
            currentPage = uiState.currentPage,
            pageCount = uiState.pageCount,
            backgroundColor = currentPageColor,
            textColor = currentOnColor,
            onForward = { onAction(Action.UpdatePage(ButtonEvent.FORWARD)) },
            onBack = { onAction(Action.UpdatePage(ButtonEvent.BACKWARD)) },
            actionText = uiState.actionText
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
    }
}

@Preview
@Composable
private fun SequentialOnboardingScreenPreview(
    @PreviewParameter(SequentialOnboardingUiStateProvider::class) uiState: SequentialOnboardingUiState,
) {
    SequentialOnboardingScreen(
        uiState = uiState,
        onAction = { }
    )
}

private class SequentialOnboardingUiStateProvider :
    PreviewParameterProvider<SequentialOnboardingUiState> {
    override val values: Sequence<SequentialOnboardingUiState> = sequenceOf(
        SequentialOnboardingUiState(
            steps = listOf(
                Step(
                    title = "Step 1",
                    description = "Description 1",
                    icon = edu.stanford.spezi.modules.design.R.drawable.ic_assignment
                ),
                Step(
                    title = "Step 2",
                    description = "Description 2",
                    icon = edu.stanford.spezi.modules.design.R.drawable.ic_groups
                ),
                Step(
                    title = "Step 3",
                    description = "Description 3",
                    icon = edu.stanford.spezi.modules.design.R.drawable.ic_medication
                )
            ),
            currentPage = 0,
        ),
        SequentialOnboardingUiState(
            steps = listOf(
                Step(
                    title = "Step 1",
                    description = "Description 1",
                    icon = edu.stanford.spezi.modules.design.R.drawable.ic_bluetooth
                ),
                Step(
                    title = "Step 2",
                    description = "Description 2",
                    icon = edu.stanford.spezi.modules.design.R.drawable.ic_bluetooth
                )
            ),
            currentPage = 1,
        )
    )
}

enum class SequentialOnboardingScreenTestIdentifier {
    ROOT,
    PAGER,
    PAGE,
    PAGE_INDICATOR,
}
