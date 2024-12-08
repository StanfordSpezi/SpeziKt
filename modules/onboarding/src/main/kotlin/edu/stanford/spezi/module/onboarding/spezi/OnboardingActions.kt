package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert

@Composable
fun OnboardingActions(
    primaryText: String,
    primaryAction: suspend () -> Unit,
    secondaryText: String? = null,
    secondaryAction: (suspend () -> Unit)? = null,
) {
    val primaryActionState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val secondaryActionState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    ViewStateAlert(primaryActionState)
    ViewStateAlert(secondaryActionState)

    Column(Modifier.padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        SuspendButton(state = primaryActionState, action = primaryAction) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 38.dp)
            ) {
                Text(
                    primaryText,
                    textAlign = TextAlign.Center,
                )
            }
        }

        secondaryText?.let { secondaryText ->
            secondaryAction?.let { secondaryAction ->
                // TODO: Make SuspendTextButton
                SuspendButton(state = secondaryActionState, action = secondaryAction) {
                    Text(secondaryText)
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun OnboardingActionsPreview() {
    SpeziTheme(isPreview = true) {
        OnboardingActions(
            "Primary",
            { println("Primary Action") },
            "Secondary",
            { println("Secondary Action") }
        )
    }
}
