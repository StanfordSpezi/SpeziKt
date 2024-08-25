plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "edu.stanford.bdh.engagehf"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "edu.stanford.bdh.engagehf"
        versionCode =
            (project.findProperty("android.injected.version.code") as? String)?.toInt() ?: 1
        versionName = (project.findProperty("android.injected.version.name") as? String) ?: "1.0.0"
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
            isMinifyEnabled = false
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", "false")
        }
        debug {
            // Disabling coverage due to: https://github.com/hapifhir/org.hl7.fhir.core/issues/1688
            enableAndroidTestCoverage = false
            buildConfigField("boolean", "USE_FIREBASE_EMULATOR", "true")
        }
    }
}

dependencies {
    implementation(project(":core:bluetooth"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:navigation"))
    implementation(project(":modules:account"))
    implementation(project(":modules:education"))
    implementation(project(":modules:healthconnectonfhir"))
    implementation(project(":modules:onboarding"))

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)

    implementation(libs.androidx.core.i18n)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.view.model.ktx)
    implementation(libs.androidx.splashscreen)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.navigation.compose)
    implementation(libs.vico.compose.m3)

    androidTestImplementation(project(":core:testing"))
}
