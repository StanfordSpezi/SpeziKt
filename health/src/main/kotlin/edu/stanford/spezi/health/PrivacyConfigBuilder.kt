package edu.stanford.spezi.health

import edu.stanford.spezi.core.SpeziDsl
import edu.stanford.spezi.health.internal.PrivacyConfig
import edu.stanford.spezi.ui.ComposableBlock
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource

@SpeziDsl
class PrivacyConfigBuilder {
    internal var config: PrivacyConfig = PrivacyConfig.Default

    fun explanationText(title: StringResource, description: StringResource) {
        config = PrivacyConfig.Text(title = title, description = description)
    }

    fun composable(block: ComposableBlock) {
        config = PrivacyConfig.Composable(block)
    }

    fun content(content: ComposableContent) {
        config = PrivacyConfig.Content(content)
    }
}
