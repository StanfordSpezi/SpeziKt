package edu.stanford.spezi.modules.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.testIdentifier

@Composable
fun CenteredBoxContent(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .testIdentifier(CenteredBoxContentTestIdentifier.ROOT)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

enum class CenteredBoxContentTestIdentifier {
    ROOT,
}
