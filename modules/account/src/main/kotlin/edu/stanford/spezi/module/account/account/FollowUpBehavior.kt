package edu.stanford.spezi.module.account.account

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

enum class FollowUpBehavior {
    DISABLED, MINIMAL, REDUNDANT;

    companion object {
        val automatic: FollowUpBehavior get() = MINIMAL
    }
}

val localFollowUpBehaviorAfterSetup = compositionLocalOf { FollowUpBehavior.automatic }
