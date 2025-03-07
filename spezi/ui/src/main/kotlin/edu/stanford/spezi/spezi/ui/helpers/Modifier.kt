package edu.stanford.spezi.spezi.ui.helpers

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics

fun Modifier.testIdentifier(identifier: TestIdentifier, suffix: String? = null) =
    testTag(tag = identifier.tag(suffix = suffix))

fun Modifier.imageResourceIdentifier(identifier: String) = testTag(tag = identifier)
    .semantics { this[ImageResourceKey] = identifier }
