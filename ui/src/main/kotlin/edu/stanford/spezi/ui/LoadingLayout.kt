package edu.stanford.spezi.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import edu.stanford.spezi.ui.theme.Colors.primary

@Composable
fun LoadingLayout(
    color: Color = primary,
) {
    CenteredBoxContent {
        CircularProgressIndicator(color = color)
    }
}
