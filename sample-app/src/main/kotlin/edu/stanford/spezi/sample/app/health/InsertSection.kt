package edu.stanford.spezi.sample.app.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles

data class InsertSection(
    val title: String,
    val description: String,
    val enabled: Boolean,
    val onClick: () -> Unit,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.small)
        ) {
            Text(
                text = title,
                style = TextStyles.headlineSmall
            )

            Text(
                text = description,
                style = TextStyles.bodyMedium
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
                enabled = enabled,
            ) {
                Text(text = "Insert sample step record")
            }
        }
    }
}
