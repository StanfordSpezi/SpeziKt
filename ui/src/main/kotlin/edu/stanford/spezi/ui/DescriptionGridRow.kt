package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DescriptionGridRow(
    description: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacings.small),
        horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .alignByBaseline()
        ) {
            description()
        }

        Box(
            modifier = Modifier
                .alignByBaseline()
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@ThemePreviews
@Composable
private fun DescriptionGridRowPreviews() {
    SpeziTheme {
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
}
