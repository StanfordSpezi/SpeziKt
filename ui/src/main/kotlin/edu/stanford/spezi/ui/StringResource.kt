@file:Suppress("SpreadOperator")

package edu.stanford.spezi.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

sealed interface StringResource {

    @Immutable
    private data class TextStringResource(private val text: String) : StringResource {
        override fun get(context: Context): String = text
    }

    @Immutable
    private data class ContextStringResource(
        @StringRes private val id: Int,
        private val args: List<Any>,
    ) : StringResource {
        override fun get(context: Context): String = context.getString(id, *args.toTypedArray())
    }

    fun get(context: Context): String

    @Composable
    @ReadOnlyComposable
    fun text(): String = get(LocalContext.current)

    companion object {
        operator fun invoke(@StringRes id: Int, vararg args: Any): StringResource =
            ContextStringResource(id, args.toList())

        operator fun invoke(text: String): StringResource =
            TextStringResource(text)
    }
}
