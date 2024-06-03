@file:Suppress("Filename")
package edu.stanford.spezi.core.utils

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
