package edu.stanford.spezi.core.testing

import androidx.compose.ui.test.SemanticsMatcher
import edu.stanford.spezi.core.utils.extensions.ImageResourceKey

fun hasIconIdentifier(expectedIdentifier: String) =
    SemanticsMatcher.expectValue(ImageResourceKey, expectedIdentifier)
