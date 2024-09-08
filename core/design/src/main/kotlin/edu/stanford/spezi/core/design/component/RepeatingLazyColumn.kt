package edu.stanford.spezi.core.design.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

private const val DEFAULT_REPEATING_ITEMS_COUNT = 7

/**
 * Renders a lazy column with [itemCount] same [content]s. Helpful when rendering loading list items
 *
 * @param modifier Modifier to be applied
 * @param itemCount count of items to be rendered, defaults to 7
 * @param content content of the item
 */
@Composable
fun RepeatingLazyColumn(
    modifier: Modifier = Modifier,
    itemCount: Int = DEFAULT_REPEATING_ITEMS_COUNT,
    content: @Composable LazyItemScope.() -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(itemCount) { content() }
    }
}

@ThemePreviews
@Composable
fun RepeatingLazyColumnPreview() {
    SpeziTheme {
        RepeatingLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium),
            itemCount = 100,
            content = {
                Text(
                    text = "#same",
                    modifier = Modifier.padding(Spacings.medium)
                )
            }
        )
    }
}
