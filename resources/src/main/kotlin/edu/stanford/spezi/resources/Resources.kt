package edu.stanford.spezi.resources

/**
 * Type alias for [R.string], providing a centralised entry point for all string resource IDs.
 *
 * All string resources are defined in the `:resources` module and accessed through this alias
 * instead of referencing a module-specific `R` class directly.
 *
 * Example usage:
 * ```kotlin
 * import edu.stanford.spezi.resources.Strings
 *
 * StringResource(Strings.account_login_button)
 * ```
 */
typealias Strings = R.string

/**
 * Type alias for [R.drawable], providing a centralised entry point for all drawable resource IDs.
 *
 * All drawable resources are defined in the `:resources` module and accessed through this alias
 * instead of referencing a module-specific `R` class directly.
 *
 * Example usage:
 * ```kotlin
 * import edu.stanford.spezi.resources.Drawables
 *
 * ImageResource(Drawables.ic_google)
 * ```
 */
typealias Drawables = R.drawable
