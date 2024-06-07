plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("kotlin-parcelize") // needed only for non-primitive classes
}

android {
    namespace = "edu.stanford.spezi.app"

    defaultConfig {
        applicationId = "edu.stanford.spezi.app"
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", "false")
        }
        debug {
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", "true")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.view.model.ktx)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":core:bluetooth"))
    implementation(project(":modules:onboarding"))
    implementation(project(":core:coroutines"))
    implementation(project(":modules:account"))
    implementation(project(":core:navigation"))
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    androidTestImplementation(project(":core:testing"))
}