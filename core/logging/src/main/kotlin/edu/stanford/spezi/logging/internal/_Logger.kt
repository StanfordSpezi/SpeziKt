package edu.stanford.spezi.logging.internal

sealed interface _Logger {
    val tag: String
    val messagePrefix: String?
    var nextTag: String?

    fun i(throwable: Throwable?, message: () -> String)
    fun w(throwable: Throwable?, message: () -> String)
    fun e(throwable: Throwable?, message: () -> String)
}

internal fun _Logger.getCurrentTag(): String {
    val currentTag = nextTag ?: tag
    return currentTag.also { nextTag = null }
}

internal fun _Logger.getMessage(message: () -> String) = if (messagePrefix != null) {
    "$messagePrefix - ${message()}"
} else {
    message()
}