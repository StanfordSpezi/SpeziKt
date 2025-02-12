package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf

sealed interface AccountViewType {
    data object Signup : AccountViewType
    data class Overview(val mode: OverviewEntryMode) : AccountViewType

    enum class OverviewEntryMode {
        NEW, EXISTING, DISPLAY
    }

    val isEnteringNewData: Boolean get() = when (this) {
        Signup -> true
        is Overview -> mode == OverviewEntryMode.NEW
    }
}

val LocalAccountViewType = compositionLocalOf<AccountViewType?> { null }
