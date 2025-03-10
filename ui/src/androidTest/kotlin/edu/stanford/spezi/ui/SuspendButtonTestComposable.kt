package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            SuspendButton("Hello World") {
                delay(500.milliseconds)
                showCompleted = true
            }

            SuspendButton("Hello Throwing World", viewState) {
                throw CustomError()
            }
        }
    }
}
