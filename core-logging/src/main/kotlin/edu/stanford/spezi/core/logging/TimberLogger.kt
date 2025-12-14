package edu.stanford.spezi.core.logging

import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A logger implementation that delegates logging to Timber.
 *
 * @property tag The tag to be used for logging.
 * @property messagePrefix The optional prefix to be added to each logged message.
 */
internal class TimberLogger(
    override val tag: String,
    override val messagePrefix: String?,
) : _Logger {
    override var nextTag: String? = null

    init { plant() }

    override fun i(throwable: Throwable?, message: () -> String) {
        Timber.tag(getCurrentTag()).i(throwable, getMessage(message))
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        Timber.tag(getCurrentTag()).w(throwable, getMessage(message))
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        Timber.tag(getCurrentTag()).e(throwable, getMessage(message))
    }

    companion object {
        private val PLANTED_DEBUG_TREE = AtomicBoolean(false)

        fun plant() {
            if (PLANTED_DEBUG_TREE.getAndSet(true).not()) Timber.plant(Timber.DebugTree())
        }
    }
}
