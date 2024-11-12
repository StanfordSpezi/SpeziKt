package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf

sealed interface AccountViewType {
    data object Signup : AccountViewType
    data class Overview(val mode: OverviewEntryMode) : AccountViewType

    enum class OverviewEntryMode {
        NEW, EXISTING, DISPLAY
    }
}

val LocalAccountViewType = compositionLocalOf<AccountViewType?> { null }
