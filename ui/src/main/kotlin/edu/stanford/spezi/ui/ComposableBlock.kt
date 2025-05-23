package edu.stanford.spezi.ui

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
 * A type alias for a composable lambda function with no parameters and a return value of type T.
 * Useful for types of properties of a [ComposableContent] where a compose scope is needed
 *
 * Example usage:
 *
 * ```kotlin
 * data class MyButton(
 *     private val title: String,
 *     private val containerColor: ComposeValue<Color> = { Colors.primary },
 *     private val onClick: () -> Unit,
 * ) : ComposableContent
 * ```
 */
typealias ComposeValue<T> = @Composable () -> T
