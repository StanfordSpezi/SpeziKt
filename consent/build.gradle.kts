plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.consent"
}

dependencies {
    api(libs.bundles.compose.androidTest)

    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    api(project(":ui"))
    api(project(":ui-markdown"))
    api(project(":onboarding"))

    implementation(libs.androidx.fragment.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.accompanist.pager)
    androidTestImplementation(project(":testing-ui"))
}
