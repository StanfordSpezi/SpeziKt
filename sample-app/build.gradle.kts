plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "edu.stanford.spezi.sample.app"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "edu.stanford.spezi.sample.app"
        versionCode = 1
        versionName = "1.0.0"
        targetSdk = libs.versions.targetSdk.get().toInt()

        vectorDrawables {
            useSupportLibrary = true
        }
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

dependencies {
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":health"))
    implementation(project(":ui"))
    androidTestImplementation(project(":testing-ui"))
}
