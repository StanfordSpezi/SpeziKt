package edu.stanford.spezi.spezi.ui.helpers

import androidx.compose.runtime.Composable

/**
 * A type alias for a composable lambda function with no parameters and no return value.
 *
 * This type alias represents a composable block of code, which can be passed around as a function.
 * It is useful for defining reusable composable blocks in Jetpack Compose.
 *
 * Example usage:
 * ```kotlin
 * val myComposable: ComposableBlock = {
 *     Text("Hello, World!")
 * }
 *
 * @Composable
 * fun MyScreen(content: ComposableBlock) {
 *     Column {
 *         content()
 *     }
 * }
 * ```
 */
typealias ComposableBlock = @Composable () -> Unit

/**
 * A type alias on any enum type. Useful to set test tag on composable of a Screen to
 * ensure uniqueness of the tags, see [edu.stanford.spezi.spezi.ui.helpers.tag]
 */
typealias TestIdentifier = Enum<*>
