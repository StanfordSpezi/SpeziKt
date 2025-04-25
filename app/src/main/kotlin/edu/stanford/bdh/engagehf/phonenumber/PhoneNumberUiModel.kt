package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.modules.design.component.AsyncButton
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ComposeValue
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class PhoneNumberUiModel(
    val phoneNumber: String,
    private val coroutineScope: ComposeValue<CoroutineScope> = { rememberCoroutineScope() },
    val onDeleteClicked: suspend () -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        var deleting by remember { mutableStateOf(false) }
        val scope = coroutineScope()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(phoneNumber)
            AsyncButton(
                modifier = Modifier.minimumInteractiveComponentSize(),
                isLoading = deleting,
                containerColor = Colors.transparent,
                contentColor = ButtonDefaults.buttonColors().containerColor,
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    deleting = true
                    scope.launch {
                        onDeleteClicked()
                        deleting = false
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        PhoneNumberUiModel(
            phoneNumber = "+1 234 567 8900",
            onDeleteClicked = {}
        ).Content()
    }
}
