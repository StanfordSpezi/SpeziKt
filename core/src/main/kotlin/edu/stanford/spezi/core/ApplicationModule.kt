package edu.stanford.spezi.core

/**
 * An automatically registered module during app start up that provides access to the spezi application instance.
 */
class ApplicationModule internal constructor(val application: SpeziApplication) : Module {
    override fun equals(other: Any?): Boolean {
        return other is ApplicationModule && other.application == application
    }

    override fun hashCode(): Int {
        return application.hashCode()
    }

    override fun toString(): String {
        return "ApplicationModule[${application.applicationContext?.packageName ?: "null"}]"
    }
}
