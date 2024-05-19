# Logging module

This module provides a flexible logging utility designed to handle various logging strategies. 
It includes a utility class `SpeziLogger` for logging messages using inline functions to avoid unnecessary memory allocation 
for large string messages. The logging utility allows for configuration of logger settings and supports different 
logging strategies through the use of tags and configurations.

### Dependency
It should be noted that this module is included by default as implementation in `spezikt.application` and `spezikt.library`
convention plugins. If none of these plugins have been applied to module, the dependency can be added as follows:

```gradle
dependencies {
    implementation(project(":core:logging"))
}
```

### Example usage

First, it is required that applications enable the logging functionality, e.g. based on build type:

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = BuildConfig.DEBUG)
    }
}
```

After that, there are several ways to make use of the logging API as shown in the example class below:

```kotlin
class MyClass {
    /**
    * Logger that derives the tag name from the component where it is defined, in this case `MyClass`
    */
    private val logger by speziLogger()

    /**
     * Logger that uses default config and `myFeature` as tag. Note that all messages of this logger are prefixed
     * with the name of the component where it has been defined to. Useful to manage logs of the same feature produced
     * via different components.
     */
    private val groupLogger by groupLogger("myFeature") {
        // optional config
    }

    private val customLogger by speziLogger {
        tag = "CUSTOM"
        messagePrefix = "custom"
        loggingStrategy = LoggingStrategy.LOG
        // regardless of the global configuration, this logger instance will always be enabled
        forceEnabled = true
    }

    fun examples() {
        logger.i { "Example log using default config and `MyClass` as tag" }
        logger.tag("NEW_TAG").i { "Example log using default config and NEW_TAG (only for this log) as tag" }
        customLogger.i { "Example log using config passed in customLogger" }
        groupLogger.i { "Example log using default config, tag `myFeature` and prefixes the message with `MyClass - `" }
        SpeziLogger.e(Error("Something went wrong")) { "Alternative log using default log config and `edu.stanford.spezi.logger as tag`" }
    }
}
```