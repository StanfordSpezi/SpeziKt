package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsDialog(
    title: String,
    items: List<String>,
    onDismissRequest: () -> Unit,
    onOptionSelected: (Int) -> Unit,
) {
    BasicAlertDialog(
        modifier = Modifier
            .background(Colors.surface, shape = RoundedCornerShape(Spacings.medium))
            .padding(Spacings.medium),
        onDismissRequest = onDismissRequest,
        content = {
            Column {
                Text(
                    text = title,
                    style = TextStyles.titleLarge,
                    color = Colors.onSurface
                )
                VerticalSpacer()
                items.forEachIndexed { index, location ->
                    Text(
                        text = location,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(index)
                                onDismissRequest()
                            }
                            .padding(vertical = Spacings.small),
                        style = TextStyles.labelLarge
                    )
                }
            }
        })
}

@ThemePreviews
@Composable
private fun BodyPositionsDialogPreview(
) {
    SpeziTheme(isPreview = true) {
        ItemsDialog(
            title = "Title",
            items = listOf("Item 1", "Item 2", "Item 3"),
            onDismissRequest = {},
            onOptionSelected = {}
        )
    }
}
