package edu.stanford.bdh.engagehf.health.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBox(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    val swipeState = rememberSwipeToDismissBoxState()

    val iconVisible = remember(swipeState.dismissDirection) {
        swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart
    }

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = swipeState,
        backgroundContent = {
            if (iconVisible) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Icon(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        tint = Colors.error,
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        content()
    }

    if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(swipeState) {
            onDelete()
            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }
}
