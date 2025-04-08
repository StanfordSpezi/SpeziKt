package edu.stanford.bdh.engagehf

import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import edu.stanford.bdh.engagehf.application.Account
import edu.stanford.bdh.engagehf.application.Onboarding
import edu.stanford.bdh.engagehf.application.Pipeline
import edu.stanford.bdh.engagehf.application.SpeziApplication
import edu.stanford.bdh.engagehf.application.SpeziConfig
import edu.stanford.spezi.core.logging.SpeziLogger

@HiltAndroidApp
class MainApplication : SpeziApplication() {

    class CoughPipeline : Pipeline(
        listOf(
            Onboarding(),
            Account()
        )
    )

    private val enableOnboarding = true
    override val config = SpeziConfig {
        // Add modules conditionally
        if(enableOnboarding)
            +Onboarding()
        +Account(username = "John Doe")
    }



    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = BuildConfig.DEBUG)
    }
}


/*
.toBackground(
            persistanceConfig = CLAIDPersistanceConfig.onBootAutoStart(),
            specialPermissions = CLAIDSpecialPermissionsConfig.regularConfig(),
            serviceAnnotation = ServiceAnnotation(
                "Engage-HF background service",
                "Running AI in the background!",
                edu.stanford.spezi.modules.design.R.drawable.ic_medication
            )
        )
 */