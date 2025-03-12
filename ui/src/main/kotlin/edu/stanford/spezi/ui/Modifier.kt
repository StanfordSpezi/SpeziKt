package edu.stanford.spezi.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics

fun Modifier.imageResourceIdentifier(identifier: String) = this
    .testTag(tag = identifier)
    .semantics { this[ImageResourceKey] = identifier }
