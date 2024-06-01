package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles.bodyLarge
import edu.stanford.spezi.core.design.theme.TextStyles.bodyMedium
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleSmall


/**
 * The onboarding screen.
 */
@Composable
fun OnboardingScreen(
) {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
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
                style = titleLarge
            )

            Text(text = uiState.subtitle, style = bodyLarge)
            Spacer(modifier = Modifier.height(Spacings.small))

            LazyColumn {
                items(uiState.areas) { area ->
                    FeatureItem(area = area)
                    Spacer(modifier = Modifier.height(Spacings.medium))
                }
            }
        }
        Button(
            onClick = { viewModel.onAction(Action.OnLearnMoreClicked) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Learn More")
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
            contentDescription = "Area Icon",
            modifier = Modifier.size(Sizes.icon),
            tint = primary
        )
        Spacer(Modifier.width(Spacings.medium))
        Column {
            Text(
                text = area.title,
                style = titleSmall
            )
            Text(
                text = area.description,
                style = bodyMedium
            )
        }
    }
}