package edu.stanford.spezi.core.design.validation.views.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun DescriptionGridRow(
    description: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .alignByBaseline()
                .weight(1f, fill = false)
        ) {
            description()
        }

        Box(
            modifier = Modifier
                .alignByBaseline()
                .fillMaxWidth()
                .weight(1f)
        ) {
            content()
        }
    }
}

@ThemePreviews
@Composable
private fun DescriptionGridRowPreviews() {
    Column {
        DescriptionGridRow(description = {
            Text("Description")
        }) {
            Text("Content")
        }

        HorizontalDivider()

        DescriptionGridRow(description = {
            Text("Description")
        }) {
            Text("Content")
        }

        HorizontalDivider()

        DescriptionGridRow(description = {
            Text("Description")
        }) {
            Text("Content")
        }
    }
}
