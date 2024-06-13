package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import edu.stanford.spezi.core.design.theme.Spacings

@Composable
fun VerticalSpacer(height: Dp = Spacings.medium) {
    Spacer(modifier = Modifier.height(height))
}
