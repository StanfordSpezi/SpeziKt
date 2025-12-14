package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import kotlin.reflect.KProperty

// TODO: This doesn't actually need to be a viewModifier, right?
data class SecurityRelatedComposable(
    val composable: @Composable () -> Unit,
) {
    operator fun getValue(type: Any?, property: KProperty<*>): @Composable () -> Unit {
        return composable
    }
}
