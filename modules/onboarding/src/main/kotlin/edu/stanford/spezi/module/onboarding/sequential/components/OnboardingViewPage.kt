package edu.stanford.spezi.module.onboarding.sequential.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

/**
 * A composable that represents a page in the onboarding view pager.
 * @sample edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingViewPagerScreen
 */
@Composable
fun OnboardingViewPage(
    backgroundColor: Color,
    onColor: Color,
    title: String,
    description: String,
    iconId: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(Spacings.medium)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = onColor
            )
            Spacer(modifier = Modifier.height(Spacings.large))
            Text(text = title, style = TextStyles.titleLarge, color = onColor)
            Spacer(modifier = Modifier.height(Spacings.large))
            Text(
                text = description,
                style = TextStyles.bodyLarge,
                color = onColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
fun OnboardingViewPagePreview() {
    OnboardingViewPage(
        backgroundColor = primary,
        onColor = onPrimary,
        title = "Title",
        description = "Description",
        iconId = edu.stanford.spezi.core.design.R.drawable.ic_groups,
    )
}