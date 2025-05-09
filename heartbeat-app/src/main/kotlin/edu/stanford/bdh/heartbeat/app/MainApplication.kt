package edu.stanford.bdh.heartbeat.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import edu.stanford.spezi.core.logging.SpeziLogger

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = BuildConfig.DEBUG)
    }
}
