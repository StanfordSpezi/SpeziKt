package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
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
                actions = actions
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                content = content
            )
        }
    )
}

@ThemePreviews
@Composable
private fun CommonScaffoldPreview() {
    SpeziTheme {
        CommonScaffold(
            title = "Common Scaffold",
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "",
                    )
                }
            },
            content = {}
        )
    }
}
