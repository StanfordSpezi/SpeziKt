package edu.stanford.spezi.core.logging

/**
 * An enum class representing different logging strategies.
 *
 * This enum class defines the available logging strategies, which determine how log messages are handled.
 */
enum class LoggingStrategy {
    /**
     * The logging strategy that prints messages to the standard output stream.
     */
    PRINT,

    /**
     * The logging strategy that delegates logging to Timber.
     */
    TIMBER,

    /**
     * The logging strategy that logs messages using the Log utility.
     */
    LOG,
}
