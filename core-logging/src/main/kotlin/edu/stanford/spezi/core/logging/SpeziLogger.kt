package edu.stanford.spezi.core.logging

import java.util.concurrent.atomic.AtomicBoolean

/**
 * A utility class for logging messages using different logging strategies.
 *
 * This class provides inline functions for logging with crossinline lambdas to preserve
 * the control flow within the logging methods. By using inline functions, large string messages
 * are not allocated in memory unnecessarily, optimizing performance.
 *
 * @property tag The tag to be used for logging.
 * @property config The configuration for logger settings.
 *
 * @see [speziLogger] and [groupLogger] for configuration options and how to create [SpeziLogger] instances.
 *
 */
class SpeziLogger internal constructor(private val tag: String, private val config: LoggerConfig) {

    /**
     * Gets the status of logging based on the global flag and logger configuration.
     */
    private val enabled get() = IS_LOGGING_ENABLED || config.get().forceEnabled

    /**
     * Lazily initializes the appropriate logger based on the configuration.
     */
    private val _logger by lazy {
        val currentConfig = config.get()
        val logTag = tag.removeSuffix("Impl")
        val prefix = currentConfig.messagePrefix
        when (currentConfig.loggingStrategy) {
            LoggingStrategy.LOG -> LogLogger(logTag, prefix)
            LoggingStrategy.PRINT -> PrintLogger(logTag, prefix)
            LoggingStrategy.TIMBER -> TimberLogger(logTag, prefix)
        }
    }

    /**
     * Retrieves the logger instance if logging is enabled.
     *
     * This property provides access to the logger instance only if logging is enabled, based on the global flag
     * and the logger configuration.
     *
     * It is recommended to use the logging methods (`i`, `w`, `e`) provided by this class to log messages,
     * rather than accessing the logger instance directly. This property is only public due to it's usage inside of inline
     * methods below.
     */
    @PublishedApi
    internal val logger get() = if (enabled) _logger else null

    /**
     * Logs an informational message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    inline fun i(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.i(throwable) { message() }
    }

    /**
     * Logs a warning message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    inline fun w(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.w(throwable) { message() }
    }

    /**
     * Logs an error message.
     *
     * @param throwable Optional throwable associated with the message.
     * @param message A function providing the message to be logged.
     */
    inline fun e(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.e(throwable) { message() }
    }

    /**
     * Sets the next tag for logging.
     *
     * @param tag The next tag to be used for logging.
     * @return This [SpeziLogger] instance with the next tag set.
     */
    fun tag(tag: String) = apply {
        logger?.nextTag = tag
    }

    /**
     * Creates a new [SpeziLogger] instance with the specified message prefix.
     *
     * @param prefix The message prefix to be added to log messages.
     * @return A new [SpeziLogger] instance with the specified message prefix.
     */
    fun withMessagePrefix(prefix: String): SpeziLogger = SpeziLogger(
        tag = tag,
        config = config.apply { messagePrefix = prefix }
    )

    companion object {
        private val _IS_LOGGING_ENABLED = AtomicBoolean(false)
        internal val IS_LOGGING_ENABLED get() = _IS_LOGGING_ENABLED.get()

        /**
         * Use this property if needed to override all logger configs for all logs during development
         * The property must be set back to null when pushing (Unit tested)!!!
         *
         * internal val GLOBAL_CONFIG: (LoggerConfig.() -> Unit)? = {
         *     tag = "my_tag"
         *     messagePrefix = "my_log"
         *     loggingStrategy = LoggingStrategy.LOG
         * }
         */
        internal val GLOBAL_CONFIG: (LoggerConfig.() -> Unit)? = null

        /**
         * The logger instance with default configuration.
         *
         * It is recommended to use the logging methods (`i`, `w`, `e`) provided by this class to log messages,
         * rather than accessing the logger instance directly. This property is only public due to it's usage inside of inline
         * methods below.
         */
        @PublishedApi
        internal val LOGGER by speziLogger {
            tag = "edu.stanford.spezi.logger"
            messagePrefix = null
            loggingStrategy = LoggingStrategy.TIMBER
            forceEnabled = false
        }

        /**
         * Logs an informational message with tag `edu.stanford.spezi.logger` using [LoggingStrategy.TIMBER].
         *
         * @param throwable Optional throwable associated with the message.
         * @param message A function providing the message to be logged.
         */
        inline fun i(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.i(throwable, message)
        }

        /**
         * Logs a warning message with tag `edu.stanford.spezi.logger` using [LoggingStrategy.TIMBER].
         *
         * @param throwable Optional throwable associated with the message.
         * @param message A function providing the message to be logged.
         */
        inline fun w(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.w(throwable, message)
        }

        /**
         * Logs an error message with tag `edu.stanford.spezi.logger` using [LoggingStrategy.TIMBER].
         *
         * @param throwable Optional throwable associated with the message.
         * @param message A function providing the message to be logged.
         */
        inline fun e(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.e(throwable, message)
        }

        /**
         * Sets the next tag for logging.
         *
         * @param tag The next tag to be used for logging.
         * @return This [SpeziLogger] instance with the next tag set.
         */
        fun tag(tag: String) = LOGGER.tag(tag)

        /**
         * Global configuration setting whether logging is enabled. If enabled, for all logger instances
         * using [LoggingStrategy.TIMBER] it will plant `Timber.DebugTree()`
         *
         * @param enabled Boolean flag indicating whether logging is enabled.
         */
        fun setLoggingEnabled(enabled: Boolean) {
            _IS_LOGGING_ENABLED.set(enabled)
        }
    }
}
