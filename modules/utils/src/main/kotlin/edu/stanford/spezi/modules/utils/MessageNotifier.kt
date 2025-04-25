package edu.stanford.spezi.modules.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    fun notify(message: String, duration: Duration = Duration.SHORT) {
        val toast = { Toast.makeText(context, message, duration.value).show() }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast()
        } else {
            mainHandler.post { toast() }
        }
    }

    fun notify(@StringRes messageId: Int, duration: Duration = Duration.SHORT) {
        notify(message = context.getString(messageId), duration = duration)
    }

    enum class Duration(internal val value: Int) {
        LONG(Toast.LENGTH_LONG),
        SHORT(Toast.LENGTH_SHORT),
    }
}
