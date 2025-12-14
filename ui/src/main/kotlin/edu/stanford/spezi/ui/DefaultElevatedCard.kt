package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Sizes

/**
 * Default Material Design elevated card.
 *
 * @param modifier Modifier to be applied
 * @param shape card shape
 * @param content content of the card
 */
@Composable
fun DefaultElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    content: @Composable (ColumnScope.() -> Unit),
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = Sizes.Elevation.medium,
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Colors.surface.lighten(),
        ),
        content = content,
    )
}
