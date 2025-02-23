package edu.stanford.spezi.core.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ComposableContent {
    val body
        @Composable
        get() = Body(Modifier)

    @Composable
    fun Body(modifier: Modifier)
}

data class ComposableContentBuilder(
    private val builder: ComposeValue<ComposableContent>
) : ComposableContent {
    @Composable
    override fun Body(modifier: Modifier) {
        builder.invoke().Body(modifier)
    }
}