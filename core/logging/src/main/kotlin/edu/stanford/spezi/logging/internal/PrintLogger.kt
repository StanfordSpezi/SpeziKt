package edu.stanford.spezi.logging.internal

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
        append(getMessage(message) )
        throwable?.let {
            append(" Error: $it")
        }
    }
}