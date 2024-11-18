package edu.stanford.spezi.core.design.views.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class CustomError : Throwable() {
    override val message = "Error was thrown!"
}

@Composable
fun SuspendButtonTestComposable() {
    var showCompleted by remember { mutableStateOf(false) }
    val viewState = remember { mutableStateOf<ViewState>(ViewState.Idle) }

    ViewStateAlert(viewState)

    Column {
        if (showCompleted) {
            Text("Action executed")
            Button(onClick = { showCompleted = false }) {
                Text("Reset")
            }
        } else {
            SuspendButton(StringResource("Hello World")) {
                delay(500.milliseconds)
                showCompleted = true
            }

            SuspendButton(StringResource("Hello Throwing World"), viewState) {
                throw CustomError()
            }
        }
    }
}
