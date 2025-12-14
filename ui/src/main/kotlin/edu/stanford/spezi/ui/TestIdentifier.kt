package edu.stanford.spezi.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A type alias on any enum type. Useful to set test tag on composable of a Screen to
 * ensure uniqueness of the tags
 */
typealias TestIdentifier = Enum<*>

/**
 * Sets the test tag of a composable to the tag of the enum case.
 */
fun Modifier.testIdentifier(identifier: TestIdentifier, suffix: String? = null) =
    testTag(tag = identifier.tag(suffix = suffix))

/**
 * Constructs the test tag of an enum as `EnumTypeName:EnumCaseName`
 */
fun TestIdentifier.tag(suffix: String? = null): String = "${javaClass.simpleName}:$name${suffix ?: ""}"
