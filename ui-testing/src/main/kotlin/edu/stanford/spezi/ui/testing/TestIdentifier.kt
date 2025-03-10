package edu.stanford.spezi.ui.testing

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag

/**
 * A type alias on any enum type. Useful to set test tag on composable of a Screen to
 * ensure uniqueness of the tags, see [edu.stanford.spezi.spezi.ui.helpers.tag]
 */
typealias TestIdentifier = Enum<*>

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

/**
 * Constructs the test tag of an enum as `EnumTypeName:EnumCaseName`
 */
fun TestIdentifier.tag(suffix: String? = null): String = "${javaClass.simpleName}:$name${suffix ?: ""}"

fun Modifier.testIdentifier(identifier: TestIdentifier, suffix: String? = null) =
    testTag(tag = identifier.tag(suffix = suffix))
