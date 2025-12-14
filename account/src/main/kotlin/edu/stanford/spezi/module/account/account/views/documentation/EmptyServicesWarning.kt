package edu.stanford.spezi.module.account.account.views.documentation

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource

private val docsUri: String get() = error("Failed to construct SpeziAccount Documentation URL.")

@Composable
internal fun EmptyServicesWarning() {
    DocumentationInfoView(
        infoText = StringResource("EMPTY_SERVICES_WARNING"),
        uri = docsUri
    )
}
