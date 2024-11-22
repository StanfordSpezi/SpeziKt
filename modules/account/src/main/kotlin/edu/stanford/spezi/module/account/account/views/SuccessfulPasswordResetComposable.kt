package edu.stanford.spezi.module.account.account.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun SuccessfulPasswordResetComposable(
    label: StringResource? = null
) {
    val text = label ?: StringResource("UAP_RESET_PASSWORD_PROCESS_SUCCESSFUL_LABEL")

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        ImageResourceComposable(
            ImageResource.Vector(Icons.Default.CheckCircle, StringResource("Checkmark")),
            modifier = Modifier.size(100.dp).padding(bottom = 32.dp),
            tint = Color.Green.copy(alpha = 0.7f),
        )
        Text(
            text.text(),
            textAlign = TextAlign.Center,
        )
    }
}

@ThemePreviews
@Composable
private fun SuccessfulPasswordResetComposablePreview() {
    SpeziTheme(isPreview = true) {
        SuccessfulPasswordResetComposable(
            StringResource("Sent out a link to reset the password.")
        )
    }
}
