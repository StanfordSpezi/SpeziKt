package edu.stanford.spezi.core.testing

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import edu.stanford.spezi.core.utils.extensions.ImageResourceKey

fun SemanticsNodeInteraction.assertImageIdentifier(identifier: String) = assert(
    SemanticsMatcher.expectValue(ImageResourceKey, identifier)
)
