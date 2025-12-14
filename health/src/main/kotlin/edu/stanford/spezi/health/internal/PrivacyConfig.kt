package edu.stanford.spezi.health.internal

import edu.stanford.spezi.ui.ComposableBlock
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource

internal sealed interface PrivacyConfig {
    data object Default : PrivacyConfig
    data class Text(
        val title: StringResource,
        val description: StringResource,
    ) : PrivacyConfig
    data class Content(val content: ComposableContent) : PrivacyConfig
    data class Composable(val composable: ComposableBlock) : PrivacyConfig
}
