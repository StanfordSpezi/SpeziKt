plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.bdh.heartbeat.app"

    defaultConfig {
        applicationId = "edu.stanford.bdh.heartbeat.app"
        versionCode = 1
        versionName = "1.0.0"
        targetSdk = libs.versions.targetSdk.get().toInt()

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
        debug {
            isMinifyEnabled = false
        }
    }
}
