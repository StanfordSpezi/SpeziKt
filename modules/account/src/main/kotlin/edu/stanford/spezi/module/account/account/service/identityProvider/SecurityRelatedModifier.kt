package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable

// TODO: Think about whether Modifier is the right word here...
// TODO: Possibly think about using actual "Modifier" type here?!
data class SecurityRelatedModifier(
    val modifier: @Composable (content: @Composable () -> Unit) -> Unit
)
