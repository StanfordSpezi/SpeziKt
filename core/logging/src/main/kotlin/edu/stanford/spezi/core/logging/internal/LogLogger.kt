package edu.stanford.spezi.core.logging.internal

import android.annotation.SuppressLint
import android.util.Log

/**
 * A logger implementation that logs messages using the Log utility.
 *
 * @property tag The tag to be used for logging.
 * @property messagePrefix The optional prefix to be added to each logged message.
 */
@SuppressLint("LogNotTimber")
internal class LogLogger(
    override val tag: String,
    override val messagePrefix: String?,
) : _Logger {
    override var nextTag: String? = null

    override fun i(throwable: Throwable?, message: () -> String) {
        Log.i(getCurrentTag(), getMessage(message), throwable)
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        Log.w(getCurrentTag(), getMessage(message), throwable)
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        Log.d(getCurrentTag(), getMessage(message), throwable)
    }
}
