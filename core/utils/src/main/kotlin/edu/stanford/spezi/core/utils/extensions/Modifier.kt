package edu.stanford.spezi.core.utils.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import edu.stanford.spezi.core.utils.TestIdentifier

fun Modifier.testIdentifier(identifier: TestIdentifier, suffix: String? = null) =
    testTag(tag = identifier.tag(suffix = suffix))

fun Modifier.imageResourceIdentifier(identifier: String) = testTag(tag = identifier)
    .semantics { this[ImageResourceKey] = identifier }
