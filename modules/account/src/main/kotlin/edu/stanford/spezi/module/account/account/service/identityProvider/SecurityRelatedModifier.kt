package edu.stanford.spezi.module.account.account.service.identityProvider

import androidx.compose.runtime.Composable
import kotlin.reflect.KProperty

interface ComposableModifier {
    @Composable
    fun Body(content: @Composable () -> Unit)
}

// TODO: Think about whether Modifier is the right word here...
// TODO: Possibly think about using actual "Modifier" type here?!
data class SecurityRelatedModifier(
    val modifier: () -> ComposableModifier,
) {
    operator fun getValue(type: Any?, property: KProperty<*>): ComposableModifier {
        return modifier()
    }
}
