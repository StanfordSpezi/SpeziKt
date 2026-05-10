package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying a labeled horizontal divider.
 *
 * @param label The label to display.
 */
data class LabeledHorizontalDivider(
    val label: StringResource,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacings.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = label.text(),
                textAlign = TextAlign.Center,
                style = TextStyles.bodyMedium,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    SpeziTheme {
        LabeledHorizontalDivider(label = StringResource("or")).Content()
    }
}
