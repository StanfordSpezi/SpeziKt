package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf

enum class FollowUpBehavior {
    DISABLED, MINIMAL, REDUNDANT;

    companion object {
        val automatic: FollowUpBehavior get() = MINIMAL
    }
}

val LocalFollowUpBehaviorAfterSetup = compositionLocalOf { FollowUpBehavior.automatic }
