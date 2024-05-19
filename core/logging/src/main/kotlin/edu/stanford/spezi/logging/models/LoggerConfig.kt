package edu.stanford.spezi.logging.models

import edu.stanford.spezi.logging.SpeziLogger

class LoggerConfig internal constructor() {
    var tag: String? = null
    var messagePrefix: String? = null
    var forceEnabled = false
    var loggingStrategy = LoggingStrategy.TIMBER

    fun setOwner(owner: Any) {
        messagePrefix = owner.javaClass.simpleName
    }

    internal fun get() = apply {
        SpeziLogger.GLOBAL_CONFIG?.invoke(this)
    }
}
