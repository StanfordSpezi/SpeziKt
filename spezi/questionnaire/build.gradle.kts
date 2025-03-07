plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.spezi.questionnaire"

    buildTypes {
        debug {
            // Disabling coverage due to: https://github.com/hapifhir/org.hl7.fhir.core/issues/1688
            enableAndroidTestCoverage = false
        }
    }
}

dependencies {
    api(libs.android.fhir.data.capture)

    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))
    implementation(project(":spezi:ui"))

    implementation(libs.androidx.fragment.compose)
}
