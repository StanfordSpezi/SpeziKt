package edu.stanford.spezi.sample.app.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class ModuleEntryCard(
    val title: StringResource,
    val description: StringResource,
    val onClick: () -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        DefaultElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Column(
                modifier = Modifier.padding(Spacings.small),
                verticalArrangement = Arrangement.spacedBy(Spacings.extraSmall)
            ) {
                Text(
                    text = title.text(),
                    style = TextStyles.titleMedium
                )
                Text(
                    text = description.text(),
                    style = TextStyles.bodyMedium,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun Preview() {
    SpeziTheme {
        ModuleEntryCard(
            title = StringResource("Sample Module"),
            description = StringResource("This is a sample module description."),
            onClick = {}
        ).Content()
    }
}
