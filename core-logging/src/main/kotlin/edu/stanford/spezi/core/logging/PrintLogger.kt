package edu.stanford.spezi.core.logging

/**
 * A logger implementation that prints messages to the standard output stream.
 *
 * @property tag The tag to be used for logging.
 * @property messagePrefix The optional prefix to be added to each logged message.
 */
internal class PrintLogger(
    override val tag: String,
    override val messagePrefix: String?,
) : _Logger {
    override var nextTag: String? = null

    override fun i(throwable: Throwable?, message: () -> String) {
        print(message(message, throwable, "I"))
    }

    override fun w(throwable: Throwable?, message: () -> String) {
        print(message(message, throwable, "W"))
    }

    override fun e(throwable: Throwable?, message: () -> String) {
        print(message(message, throwable, "E"))
    }

    private fun message(
        message: () -> String,
        throwable: Throwable?,
        level: String,
    ) = buildString {
        append("($level) ")
        append("${getCurrentTag()} - ")
        append(getMessage(message))
        throwable?.let {
            append(" Error: $it")
        }
    }
}
