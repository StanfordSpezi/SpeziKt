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

    Column(Modifier.padding(top = 10.dp)) {
        SuspendButton(state = primaryActionState, action = primaryAction) {
            Text(
                primaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 38.dp)
            )
        }
        secondaryText?.let { secondaryText ->
            secondaryAction?.let { secondaryAction ->
                SuspendButton(state = secondaryActionState, action = secondaryAction) {
                    Text(secondaryText)
                }
            }
        }
    }
}
