package edu.stanford.spezikt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import edu.stanford.spezi.logging.SpeziLogger

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = BuildConfig.DEBUG)
    }
}