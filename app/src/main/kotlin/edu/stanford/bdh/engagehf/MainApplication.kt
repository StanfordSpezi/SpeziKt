package edu.stanford.bdh.engagehf

import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_sensor_data.AudioChannels
import adamma.c4dhi.claid_sensor_data.AudioEncoding
import dagger.hilt.android.HiltAndroidApp
import edu.stanford.bdh.engagehf.application.modules.Account
import edu.stanford.bdh.engagehf.application.BackgroundConfig
import edu.stanford.bdh.engagehf.application.modules.Onboarding
import edu.stanford.bdh.engagehf.application.SpeziApplication
import edu.stanford.bdh.engagehf.application.SpeziConfig
import edu.stanford.bdh.engagehf.application.cough.AudioRecorderModule
import edu.stanford.spezi.core.logging.SpeziLogger

@HiltAndroidApp
class MainApplication : SpeziApplication() {

    private val coughPipeline = SpeziConfig {
        +AudioRecorderModule(
            id = "AudioRecorder",
            channels = AudioChannels.CHANNEL_MONO,
            encoding = AudioEncoding.ENCODING_PCM_16BIT,
            bitRate = 16,
            samplingRate = 44100,
            sampleRecordingDuration = 5
        ).inputs(mapOf("AudioData" to "AudioData"))

    }

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

