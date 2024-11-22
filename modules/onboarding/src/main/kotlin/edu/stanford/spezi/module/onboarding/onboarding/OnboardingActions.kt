package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.onboarding.views.SuspendButton
import edu.stanford.spezi.module.onboarding.views.ViewState
import edu.stanford.spezi.module.onboarding.views.ViewStateAlert

@Composable
fun OnboardingActions(
    primaryText: StringResource,
    primaryAction: suspend () -> Unit,
    secondaryText: StringResource? = null,
    secondaryAction: (suspend () -> Unit)? = null,
) {
    val primaryActionState = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val secondaryActionState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    ViewStateAlert(primaryActionState)
    ViewStateAlert(secondaryActionState)

    Column(Modifier.padding(top = 10.dp)) {
        SuspendButton(primaryActionState, primaryAction) {
            Text(
                primaryText.text(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 38.dp)
            )
        }
        secondaryText?.let { secondaryText ->
            secondaryAction?.let { secondaryAction ->
                SuspendButton(secondaryActionState, secondaryAction) {
                    Text(secondaryText.text())
                }
            }
        }
    }
}
