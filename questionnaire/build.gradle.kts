plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.questionnaire"

    buildTypes {
        debug {
            // Disabling coverage due to: https://github.com/hapifhir/org.hl7.fhir.core/issues/1688
            enableAndroidTestCoverage = false
        }
    }
}

dependencies {
    api(libs.android.fhir.data.capture)
    api(libs.bundles.compose.androidTest)

    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    api(project(":ui"))

    implementation(libs.androidx.fragment.compose)
    androidTestImplementation(project(":testing-ui"))
}
