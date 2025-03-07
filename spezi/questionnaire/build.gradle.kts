plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.spezi.questionnaire"
}

dependencies {
    api(libs.android.fhir.data.capture)

    implementation(project(":spezi:core"))
    implementation(project(":spezi:ui"))

    implementation(libs.androidx.fragment.compose)
}
