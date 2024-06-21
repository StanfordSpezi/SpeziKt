package edu.stanford.spezi.core.testing

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import edu.stanford.spezi.core.utils.TestIdentifier
import edu.stanford.spezi.core.utils.extensions.tag

/**
 * Finds a semantics node identified by the given test identifier.
 */
fun ComposeTestRule.onNodeWithIdentifier(
    identifier: TestIdentifier,
    suffix: String? = null,
    useUnmergedTree: Boolean = false,
) = onNodeWithTag(identifier.tag(suffix = suffix), useUnmergedTree = useUnmergedTree)

fun ComposeTestRule.onAllNodes(
    identifier: TestIdentifier,
    useUnmergedTree: Boolean = false,
) = onAllNodesWithTag(identifier.tag(), useUnmergedTree)

fun ComposeTestRule.waitNode(
    identifier: TestIdentifier,
) = onAllNodesWithTag(identifier.tag()).fetchSemanticsNodes().isNotEmpty()
