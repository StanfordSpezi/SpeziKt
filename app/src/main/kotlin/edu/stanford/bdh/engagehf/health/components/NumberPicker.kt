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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews
import kotlinx.coroutines.launch

private val PICKER_SIZE = DpSize(height = 110.dp, width = 70.dp)

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    size: DpSize = PICKER_SIZE,
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = value - 1)

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(range.toList()) { index, item ->
                val isFocusedItem =
                    index == remember { derivedStateOf { lazyListState.firstVisibleItemIndex + 1 } }.value
                @Suppress("MagicNumber")
                Text(
                    text = item.toString(),
                    style = TextStyles.bodyLarge,
                    color = Colors.onSurface.copy(alpha = if (isFocusedItem) 1f else 0.6f),
                    modifier = Modifier
                        .padding(vertical = Spacings.small)
                        .alpha(if (isFocusedItem) 1f else 0.3f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
            onValueChange(firstVisibleItemIndex + 1)
            launch {
                lazyListState.scrollToItem(firstVisibleItemIndex)
            }
        }
    }
}

@ThemePreviews
@Composable
fun NumberPickerPreview() {
    SpeziTheme {
        NumberPicker(
            value = 5,
            onValueChange = {},
            range = 0..10,
        )
    }
}
