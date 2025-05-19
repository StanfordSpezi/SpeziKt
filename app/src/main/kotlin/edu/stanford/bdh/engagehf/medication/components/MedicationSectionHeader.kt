package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun SectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyles.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onToggleExpand
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SectionHeaderPreview() {
    SpeziTheme {
        SectionHeader(
            title = "Section Header",
            isExpanded = true,
            onToggleExpand = {},
        )
    }
}
