package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlinx.coroutines.launch

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = value - 1)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(range.toList()) { index, item ->
                @Suppress("MagicNumber")
                Text(
                    text = item.toString(),
                    style = TextStyles.bodyLarge,
                    color = if (index ==
                        remember { derivedStateOf { lazyListState.firstVisibleItemIndex + 1 } }.value
                    ) {
                        Colors.onSurface
                    } else {
                        Colors.onSurface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier
                        .padding(vertical = Spacings.small)
                        .alpha(if (index == remember { derivedStateOf { lazyListState.firstVisibleItemIndex + 1 } }.value) 1f else 0.3f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val selectedIndex = lazyListState.firstVisibleItemIndex
            onValueChange(selectedIndex + 1)
            launch {
                lazyListState.scrollToItem(selectedIndex)
            }
        }
    }
}

@ThemePreviews
@Composable
fun NumberPickerPreview() {
    SpeziTheme(isPreview = true) {
        NumberPicker(
            value = 5,
            onValueChange = {},
            range = 0..10,
            modifier = Modifier.size(height = 110.dp, width = 70.dp),
        )
    }
}
