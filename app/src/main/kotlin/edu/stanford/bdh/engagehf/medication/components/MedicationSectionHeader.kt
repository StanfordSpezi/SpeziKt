package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun SectionHeader(
    title: String,
    onToggleExpand: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = Spacings.medium),
            text = title,
            style = TextStyles.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.padding(end = Spacings.medium),
            onClick = onToggleExpand
        ) {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SectionHeaderPreview() {
    SectionHeader(
        title = "Section Header",
        onToggleExpand = {},
    )
}
