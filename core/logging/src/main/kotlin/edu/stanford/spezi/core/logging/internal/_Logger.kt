package edu.stanford.spezi.core.logging.internal

import edu.stanford.spezi.core.logging.SpeziLogger

/**
 * An interface representing a logger abstraction.
 *
 * This interface defines the basic contract for logging operations such as info, warning, and error messages.
 *
 * Note, that this interface should be ideally marked internal, however, it is not possible due to its usage inside inline functions
 * of [SpeziLogger] which requires it to be visible outside of the component for the inline function to access it.
 * @see [SpeziLogger] for the reasoning behind inline functions.
 */
@Suppress("ClassNaming")
sealed interface _Logger {
    val tag: String
    val messagePrefix: String?
    var nextTag: String?

    /**
     * Logs an informational message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    fun i(throwable: Throwable?, message: () -> String)

    /**
     * Logs a warning message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    fun w(throwable: Throwable?, message: () -> String)

    /**
     * Logs an error message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    fun e(throwable: Throwable? = null, message: () -> String)
}

/**
 * This function retrieves the current tag to be used for logging messages.
 * It considers any pending nextTag and resets the nextTag
 * property to null after retrieval.
 *
 * @return The current tag for logging.
 */
internal fun _Logger.getCurrentTag(): String {
    val currentTag = nextTag ?: tag
    return currentTag.also { nextTag = null }
}

/**
 * Formats the log message with the optional message prefix.
 *
 * This internal function formats the log message by appending the message prefix, if available,
 * to the message provided by the lambda function. If no prefix is set, the original message is returned.
 *
 * @param message The lambda function providing the message to be logged.
 * @return The formatted log message.
 */
internal fun _Logger.getMessage(message: () -> String) = if (messagePrefix != null) {
    "$messagePrefix - ${message()}"
} else {
    message()
}
