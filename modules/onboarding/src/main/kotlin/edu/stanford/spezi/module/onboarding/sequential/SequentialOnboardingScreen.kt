package edu.stanford.spezi.module.onboarding.sequential

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
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.onSecondary
import edu.stanford.spezi.core.design.theme.Colors.onTertiary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.secondary
import edu.stanford.spezi.core.design.theme.Colors.tertiary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.module.onboarding.sequential.components.OnboardingViewPage
import edu.stanford.spezi.module.onboarding.sequential.components.PageIndicator


/**
 * The screen that displays the sequential onboarding steps.
 *
 * @param viewModel The view model that handles the business logic of the screen.
 */
@Composable
fun SequentialOnboardingScreen(
    viewModel: SequentialOnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        viewModel.onAction(Action.SetPage(state.currentPage))
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
            .fillMaxSize()
            .background(currentPageColor)
    ) {
        HorizontalPager(
            state,
            modifier = Modifier.weight(1f)
        ) { step ->
            OnboardingViewPage(
                backgroundColor = currentPageColor,
                onColor = currentOnColor,
                title = uiState.steps[step].title,
                description = uiState.steps[step].description,
                iconId = uiState.steps[step].icon,
            )
        }
        PageIndicator(
            currentPage = uiState.currentPage,
            pageCount = uiState.pageCount,
            backgroundColor = currentPageColor,
            textColor = currentOnColor,
            onForward = { viewModel.onAction(Action.UpdatePage(ButtonEvent.FORWARD)) },
            onBack = { viewModel.onAction(Action.UpdatePage(ButtonEvent.BACKWARD)) }
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
    }
}