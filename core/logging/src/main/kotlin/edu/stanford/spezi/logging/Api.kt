package edu.stanford.spezi.logging

import edu.stanford.spezi.logging.models.LoggerConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A read only property that returns a logger configured via [config] lambda. Logger instances created via this function, automatically
 * derive the tag name from the component that created it.
 *
 * @param config Configuration to be applied on the logger
 */
fun speziLogger(config: LoggerConfig.() -> Unit = {}) = object : ReadOnlyProperty<Any, SpeziLogger> {
    private var field: SpeziLogger? = null

    override fun getValue(thisRef: Any, property: KProperty<*>) = field ?: synchronized(this) {
        val ownerName by lazy { with(thisRef.javaClass) { if (isAnonymousClass) name else simpleName } }
        val loggerConfig = LoggerConfig().apply(config)
        val logTag = loggerConfig.tag ?: ownerName
        SpeziLogger(logTag, loggerConfig).also { field = it }
    }
}

/**
 * A read only property that returns a logger with tag `tag` and prefixes the messages with the name of the component
 * where the property is defined. `tag` will override the tag of the config, but messagePrefix can be overriden via the
 * config
 *
 * @param tag Tag of the logger
 * @param config Configuration to be applied on the logger
 */
fun groupLogger(tag: String, config: LoggerConfig.() -> Unit = {}) = object : ReadOnlyProperty<Any, SpeziLogger> {
    private var field: SpeziLogger? = null

    override fun getValue(thisRef: Any, property: KProperty<*>) = field ?: synchronized(this) {
        val loggerConfig = LoggerConfig().apply {
            messagePrefix = with(thisRef.javaClass) { if (isAnonymousClass) name else simpleName }
            config()
        }
        SpeziLogger(tag, loggerConfig).also { field = it }
    }
}

/**
 * A global function that returns a kotlin.Lazy logger with the given tag
 */
fun speziLogger(
    tag: String,
    config: LoggerConfig.() -> Unit = {}
) = lazy { SpeziLogger(tag, LoggerConfig().apply(config)) }
