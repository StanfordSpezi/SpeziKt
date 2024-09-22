package edu.stanford.spezi.core.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun notify(message: String, duration: Duration = Duration.SHORT) {
        Toast.makeText(context, message, duration.value).show()
    }

    fun notify(@StringRes messageId: Int, duration: Duration = Duration.SHORT) {
        notify(message = context.getString(messageId), duration = duration)
    }

    enum class Duration(internal val value: Int) {
        LONG(Toast.LENGTH_LONG),
        SHORT(Toast.LENGTH_SHORT),
    }
}
