package edu.stanford.spezi.module.account.account.views.documentation

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource

private val docsUri: String get() = "docs" // error("Failed to construct SpeziAccount Documentation URL.")

@Composable
internal fun MissingAccountDetailsWarning() {
    DocumentationInfoView(
        infoText = StringResource("MISSING_ACCOUNT_DETAILS"),
        uri = docsUri
    )
}
