package edu.stanford.spezi.ui.personalinfo

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

val KeyboardOptions.Companion.NameDefault
    @Composable @ReadOnlyComposable get() =
        Default.copy(
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
            keyboardType = KeyboardType.Text
        )
