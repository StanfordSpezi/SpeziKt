package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf

enum class PreferredSetupStyle {
    AUTOMATIC, LOGIN, SIGNUP
}

val LocalPreferredSetupStyle = compositionLocalOf { PreferredSetupStyle.AUTOMATIC }
