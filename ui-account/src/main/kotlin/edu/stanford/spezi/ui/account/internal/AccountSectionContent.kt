package edu.stanford.spezi.ui.account.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.VerticalSpacer
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziShapes
import edu.stanford.spezi.ui.theme.TextStyles

/**
 * Composable rendering a section in any of the account screens
 *
 * @param modifier Modifier applied on the container
 * @param title optional title of the section
 * @param entries list of entries to be rendered
 */
@Composable
internal fun AccountSectionContent(
    modifier: Modifier = Modifier,
    title: StringResource?,
    entries: List<ComposableContent>,
) {
    Column(modifier = modifier) {
        title?.let {
            Text(
                text = it.text(),
                style = TextStyles.bodyLarge,
            )
        }
        VerticalSpacer(height = Spacings.small)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.surface, shape = SpeziShapes.medium)
                .padding(horizontal = Spacings.small),
        ) {
            entries.forEach { entry ->
                entry.Content(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacings.small)
                )
            }
        }
    }
}
