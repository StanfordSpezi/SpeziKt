package edu.stanford.spezi.logging

import edu.stanford.spezi.logging.internal.LogLogger
import edu.stanford.spezi.logging.internal.PrintLogger
import edu.stanford.spezi.logging.internal.TimberLogger
import edu.stanford.spezi.logging.models.LoggerConfig
import edu.stanford.spezi.logging.models.LoggingStrategy
import org.jetbrains.annotations.VisibleForTesting
import java.util.concurrent.atomic.AtomicBoolean

class SpeziLogger internal constructor(private val tag: String, private val config: LoggerConfig) {
    private val enabled get() = IS_LOGGING_ENABLED || config.get().forceEnabled

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

    @get:VisibleForTesting
    val logger get() = if (enabled) _logger else null

    inline fun i(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.i(throwable) { message() }
    }

    inline fun w(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.w(throwable) { message() }
    }

    inline fun e(throwable: Throwable? = null, crossinline message: () -> String) {
        logger?.e(throwable) { message() }
    }

    fun tag(tag: String) = apply {
        logger?.nextTag = tag
    }

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

        @get:VisibleForTesting
        val LOGGER by speziLogger {
            tag = "edu.stanford.spezi.logger"
            messagePrefix = null
            loggingStrategy = LoggingStrategy.TIMBER
            forceEnabled = false
        }

        inline fun i(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.i(throwable, message)
        }

        inline fun w(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.w(throwable, message)
        }

        inline fun e(throwable: Throwable? = null, crossinline message: () -> String) {
            LOGGER.e(throwable, message)
        }

        fun tag(tag: String) = LOGGER.tag(tag)

        fun setLoggingEnabled(enabled: Boolean) {
            _IS_LOGGING_ENABLED.set(enabled)
        }
    }
}