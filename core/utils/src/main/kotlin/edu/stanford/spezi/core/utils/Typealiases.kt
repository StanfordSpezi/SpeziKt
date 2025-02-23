@file:Suppress("Filename")
package edu.stanford.spezi.core.utils

import edu.stanford.spezi.core.utils.extensions.tag

/**
 * A type alias on any enum type. Useful to set test tag on composable of a Screen to
 * ensure uniqueness of the tags, see [tag]
 */
typealias TestIdentifier = Enum<*>

/**
 * A typealias for kotlin.Map with String keys and any values
 */
typealias JsonMap = Map<String, *>
