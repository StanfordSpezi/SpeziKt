plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "edu.stanford.bdh.heartbeat.app"

    defaultConfig {
        applicationId = "edu.stanford.heartbeatstudy"
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

dependencies {
    implementation(project(":foundation"))

    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    implementation(project(":ui"))
    implementation(project(":ui-testing"))

    implementation(project(":modules:design"))

    implementation(project(":storage-credential"))
    implementation(project(":storage-local"))

    implementation(libs.firebase.auth.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
}
