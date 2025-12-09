package edu.stanford.spezi.sample.app.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class PermissionStatus(
    val granted: Boolean,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Text(
            modifier = Modifier
                .background(if (granted) Color.Green else Color.Red, shape = RoundedCornerShape(Spacings.extraSmall))
                .padding(Spacings.tiny),
            color = Color.White,
            text = if (granted) "GRANTED" else "NOT GRANTED",
            style = TextStyles.labelSmall
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        PermissionStatus(
            granted = false,
        ).Content()
    }
}
