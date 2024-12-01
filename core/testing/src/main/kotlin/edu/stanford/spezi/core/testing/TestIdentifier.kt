package edu.stanford.spezi.core.testing

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import edu.stanford.spezi.core.utils.TestIdentifier
import edu.stanford.spezi.core.utils.extensions.tag

/**
 * Finds a semantics node identified by the given test identifier.
 */
fun SemanticsNodeInteractionsProvider.onNodeWithIdentifier(
    identifier: TestIdentifier,
    suffix: String? = null,
) = onNodeWithTag(identifier.tag(suffix = suffix), useUnmergedTree = true)

fun SemanticsNodeInteractionsProvider.onAllNodes(
    identifier: TestIdentifier,
) = onAllNodesWithTag(testTag = identifier.tag(), useUnmergedTree = true)

fun SemanticsNodeInteractionsProvider.waitNode(
    identifier: TestIdentifier,
) = onAllNodesWithTag(identifier.tag()).fetchSemanticsNodes().isNotEmpty()
