package edu.stanford.spezi.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class SequentialOnboardingContent(
    val title: String? = null,
    val description: String,
)

@Composable
fun SequentialOnboarding(
    title: String,
    subtitle: String? = null,
    content: List<SequentialOnboardingContent>,
    actionText: String,
    action: suspend () -> Unit,
) {
    SequentialOnboarding(
        title = {
            OnboardingTitle(
                title,
                subtitle,
            )
        },
        content = content,
        actionText = actionText,
        action = action,
    )
}

@Composable
fun SequentialOnboarding(
    title: @Composable () -> Unit,
    content: List<SequentialOnboardingContent>,
    actionText: String,
    action: suspend () -> Unit,
) {
    val currentContentIndex = remember { mutableIntStateOf(0) }
    Onboarding(
        title = {
            title()
        },
        content = {
            for (index in content.indices) {
                if (index <= currentContentIndex.intValue) {
                    SequentialOnboardingStep(index, content[index])
                }
            }
        },
        action = {
            val isDone = currentContentIndex.intValue >= content.size - 1
            OnboardingActions(
                primaryText = if (isDone) actionText else StringResource("Next").text(),
                primaryAction = {
                    if (!isDone) {
                        currentContentIndex.intValue++
                    } else {
                        action()
                    }
                }
            )
        }
    )
}

@Composable
private fun SequentialOnboardingStep(
    index: Int,
    content: SequentialOnboardingContent,
) {
    Row(modifier = Modifier
        .padding(bottom = Spacings.small)
        .background(Colors.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        .padding(horizontal = 12.dp)
        .padding(top = 4.dp, bottom = 12.dp)
        .fillMaxWidth(),
    ) {
        Row {
            Text(
                "${index + 1}",
                style = TextStyles.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Colors.background,
                modifier = Modifier
                    .padding(4.dp)
                    // TODO: Figure out how to achieve an actual circle here without using a preset size
                    .background(Colors.secondary, CircleShape)
                    .padding(8.dp)
                    .alignByBaseline(),
            )

            Column(modifier = Modifier.padding(start = Spacings.small).alignByBaseline()) {
                content.title?.let {
                    Text(
                        it,
                        style = TextStyles.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(bottom = Spacings.small),
                    )
                }

                Text(content.description)
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SequentialOnboardingPreview() {
    SpeziTheme {
        SequentialOnboarding(
            title = "Title",
            subtitle = "Subtitle",
            content = listOf(
                SequentialOnboardingContent(
                    title = "A thing to know",
                    description = "This is a first thing that you should know, read carefully!",
                ),
                SequentialOnboardingContent(
                    title = "Second thing to know",
                    description = "This is a second thing that you should know, read carefully!",
                ),
                SequentialOnboardingContent(
                    title = "Third thing to know",
                    description = "This is a third thing that you should know, read carefully!",
                ),
            ),
            actionText = "Continue",
            action = {
                println("Done!")
            }
        )
    }
}
