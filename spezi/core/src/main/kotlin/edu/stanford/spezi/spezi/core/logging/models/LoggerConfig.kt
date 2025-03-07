package edu.stanford.spezi.spezi.core.logging.models

import edu.stanford.spezi.spezi.core.logging.SpeziLogger

/**
 * Configuration class for specifying logger settings.
 *
 * This class allows configuring various aspects of logging behavior such as the tag, message prefix,
 * logging strategy, and whether logging is force-enabled. It also provides a utility method to set
 * the owner of the logger, which automatically sets the message prefix to the simple name of the owner class.
 *
 * @property tag The tag to be used for logging. Defaults to `null`.
 * @property messagePrefix The optional prefix to be added to each logged message. Defaults to `null`.
 * @property forceEnabled A flag indicating whether logging is force-enabled. Defaults to `false`.
 * @property loggingStrategy The logging strategy to be used. Defaults to [LoggingStrategy.TIMBER].
 */
class LoggerConfig internal constructor() {
    var tag: String? = null
    var messagePrefix: String? = null
    var forceEnabled = false
    var loggingStrategy = LoggingStrategy.TIMBER

    /**
     * Sets the owner of the logger, automatically setting the message prefix to the simple name of the owner class.
     *
     * @param owner The owner object to set.
     */
    fun setOwner(owner: Any) {
        messagePrefix = owner.javaClass.simpleName
    }

    /**
     * Retrieves the current logger configuration, applying any global configurations if available.
     *
     * @return The current logger configuration with applied global configurations.
     */
    internal fun get() = apply {
        SpeziLogger.GLOBAL_CONFIG?.invoke(this)
    }
}
