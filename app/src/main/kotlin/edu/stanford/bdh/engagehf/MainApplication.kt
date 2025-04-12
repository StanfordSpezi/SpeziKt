package edu.stanford.bdh.engagehf

import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_android.collectors.audio.MicrophoneCollector
import adamma.c4dhi.claid_sensor_data.AudioChannels
import adamma.c4dhi.claid_sensor_data.AudioEncoding
import ch.claid.cough_detection.CoughDetectionModule
import dagger.hilt.android.HiltAndroidApp
import edu.stanford.bdh.engagehf.application.modules.Account
import edu.stanford.bdh.engagehf.application.BackgroundConfig
import edu.stanford.bdh.engagehf.application.modules.Onboarding
import edu.stanford.bdh.engagehf.application.SpeziApplication
import edu.stanford.bdh.engagehf.application.SpeziConfig
import edu.stanford.bdh.engagehf.application.cough.AudioRecorderModule
import edu.stanford.bdh.engagehf.application.wrapModule
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.speziclaid.helper.structOf
import edu.stanford.speziclaid.module.WrappedModule
import edu.stanford.speziclaid.module.wrapModule

@HiltAndroidApp
class MainApplication : SpeziApplication() {

    private val name = ""


    private val enableOnboarding = true

    override val config = SpeziConfig {
        // Add modules conditionally
        if(enableOnboarding)
            +Onboarding()
        +Account(username = "John Doe")
        +coughPipeline
    }

    override val operatingMode = BackgroundConfig(
        specialPermissions = CLAIDSpecialPermissionsConfig.regularConfig(),
        persistanceConfig = CLAIDPersistanceConfig.onBootAutoStart(),
        serviceAnnotation = ServiceAnnotation(
            "Engage-HF background service",
            "Running AI in the background!",
            edu.stanford.spezi.modules.design.R.drawable.ic_medication
        )
    )

    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = BuildConfig.DEBUG)
    }
}

