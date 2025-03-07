package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.modules.onboarding.R
import edu.stanford.spezi.spezi.ui.helpers.testIdentifier
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors.primary
import edu.stanford.spezi.spezi.ui.helpers.theme.Sizes
import edu.stanford.spezi.spezi.ui.helpers.theme.Spacings
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles.bodyLarge
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles.bodyMedium
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles.titleLarge
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles.titleSmall
import edu.stanford.spezi.spezi.ui.helpers.theme.ThemePreviews

/**
 * The onboarding screen.
 */
@Composable
fun OnboardingView() {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    OnboardingView(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
fun OnboardingView(
    uiState: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .testIdentifier(OnboardingScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacings.medium)
        ) {
            Text(
                text = uiState.title,
                modifier = Modifier.testIdentifier(OnboardingScreenTestIdentifier.TITLE),
                style = titleLarge
            )
            Text(
                text = uiState.subtitle,
                modifier = Modifier.testIdentifier(OnboardingScreenTestIdentifier.SUBTITLE),
                style = bodyLarge
            )
            LazyColumn(
                modifier = Modifier
                    .testIdentifier(OnboardingScreenTestIdentifier.AREAS_LIST)
                    .padding(
                        top = Spacings.small,
                    ),
                verticalArrangement = Arrangement.spacedBy(Spacings.medium)
            ) {
                items(uiState.areas) { area ->
                    FeatureItem(area = area)
                }
            }
        }
        Button(
            onClick = { onAction(OnboardingAction.Continue) },
            modifier = Modifier
                .testIdentifier(OnboardingScreenTestIdentifier.LEARN_MORE_BUTTON)
                .fillMaxWidth(),
        ) {
            Text(text = uiState.continueButtonText)
        }
    }
}

@Composable
fun FeatureItem(area: Area) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacings.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = area.iconId),
            contentDescription = stringResource(R.string.area_icon),
            modifier = Modifier.size(Sizes.Icon.medium),
            tint = primary
        )
        Spacer(Modifier.width(Spacings.medium))
        Column {
            Text(
                text = area.title,
                modifier = Modifier.testIdentifier(
                    OnboardingScreenTestIdentifier.AREA_TITLE,
                    area.title
                ),
                style = titleSmall
            )
            Text(
                text = area.description,
                style = bodyMedium
            )
        }
    }
}

private class OnboardingUiStateProvider : PreviewParameterProvider<OnboardingUiState> {
    override val values: Sequence<OnboardingUiState> = sequenceOf(
        OnboardingUiState(
            title = "Welcome",
            subtitle = "Onboarding Subtitle",
            areas = listOf(
                Area(
                    title = "Area 1 Title",
                    description = "This is a description for area 1. Those descriptions are very important and can have different lengths.",
                    iconId = edu.stanford.spezi.core.design.R.drawable.ic_assignment
                ),
                Area(
                    title = "Area 2 Title",
                    description = "Short descriptions are also possible.",
                    iconId = edu.stanford.spezi.core.design.R.drawable.ic_bluetooth
                ),
                Area(
                    title = "Area 3 title",
                    description = "The colors on the screen are from the Spezi theme and if " +
                        "the user has dark mode or dynamic colors enabled, the colors will change accordingly.",
                    iconId = edu.stanford.spezi.core.design.R.drawable.ic_vital_signs
                ),
            ),
            continueButtonText = "Continue"
        ),
    )
}

@ThemePreviews
@Composable
private fun OnboardingScreenPreview(
    @PreviewParameter(OnboardingUiStateProvider::class) uiState: OnboardingUiState,
) {
    SpeziTheme {
        OnboardingView(
            uiState = uiState,
            onAction = { }
        )
    }
}

enum class OnboardingScreenTestIdentifier {
    ROOT,
    TITLE,
    SUBTITLE,
    AREAS_LIST,
    AREA_TITLE,
    LEARN_MORE_BUTTON,
}
