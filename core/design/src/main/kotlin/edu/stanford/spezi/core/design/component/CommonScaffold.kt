package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun CommonScaffold(
    title: String,
    content: @Composable (innerPadding: Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = Spacings.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title)
                    }
                },
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                content(Modifier.padding(innerPadding))
            }
        }
    )
}

@ThemePreviews
@Composable
private fun CommonScaffoldPreview() {
    SpeziTheme {
        CommonScaffold(
            title = "Common Scaffold",
            content = {}
        )
    }
}
