package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


@Composable
fun OnboardingComposable(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier.onSizeChanged { size.value = it }) {
        LazyColumn {
            item {
                Column(Modifier.heightIn(min = size.value.height.dp)) {
                    Column {
                        title()
                        content()
                    }
                    action?.let { action ->
                        Spacer(Modifier)
                        action()
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}