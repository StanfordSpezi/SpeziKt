package edu.stanford.spezi.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object SpeziShapes {
    val tiny = RoundedCornerShape(1.dp)
    val extraSmall = RoundedCornerShape(2.dp)
    val small = RoundedCornerShape(4.dp)
    val regular = RoundedCornerShape(6.dp)
    val medium = RoundedCornerShape(8.dp)
    val large = RoundedCornerShape(16.dp)
    val circle = CircleShape
}

@ThemePreviews
@Composable
private fun ShapesPreview() {
    SpeziTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ShapePreviewItem(label = "Tiny (1.dp)", shape = SpeziShapes.tiny)
            ShapePreviewItem(label = "Extra small (2.dp)", shape = SpeziShapes.extraSmall)
            ShapePreviewItem(label = "Small (4.dp)", shape = SpeziShapes.small)
            ShapePreviewItem(label = "Regular (6.dp)", shape = SpeziShapes.regular)
            ShapePreviewItem(label = "Medium (8.dp)", shape = SpeziShapes.medium)
            ShapePreviewItem(label = "Large (16.dp)", shape = SpeziShapes.large)
            ShapePreviewItem(label = "Circle shape", shape = SpeziShapes.circle)
        }
    }
}

@Composable
private fun ShapePreviewItem(label: String, shape: Shape) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = TextStyles.bodyMedium.bold()
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(
                    width = 2.dp,
                    color = Colors.primary,
                    shape = shape
                )
        )
    }
}
