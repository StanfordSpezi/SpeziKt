package edu.stanford.bdh.heartbeat.app.home

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.core.design.component.AsyncTextButton
import edu.stanford.spezi.core.design.component.ComposableContent
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.core.utils.ComposeValue

data class AccountActionItem(
    val title: String,
    val confirmation: String,
    val color: ComposeValue<Color>,
    val confirmButton: AsyncTextButton,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        var confirmationDialogDisplayed by remember { mutableStateOf(false) }
        val hideDialog = { confirmationDialogDisplayed = false }
        TextButton(onClick = { confirmationDialogDisplayed = true }) {
            Text(
                text = title,
                style = titleLarge,
                color = color()
            )
        }
        if (confirmationDialogDisplayed) {
            AlertDialog(
                onDismissRequest = hideDialog,
                title = { Text(title) },
                text = { Text(confirmation) },
                confirmButton = { confirmButton.Content(Modifier) },
                dismissButton = {
                    TextButton(onClick = hideDialog) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}
