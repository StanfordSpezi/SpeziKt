package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Colors

data class SurveyProgress(
    private val value: Float,
) : SurveyItem {

    @Composable
    override fun Content(modifier: Modifier) {
        val coercedValue = remember(value) { value.coerceIn(0f, 1f) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Colors.black20, CircleShape)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = coercedValue)
                    .background(Colors.cardinalRedLight, CircleShape)
            )
        }
    }
}
