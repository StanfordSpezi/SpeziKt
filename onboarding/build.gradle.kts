plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.onboarding"
}

dependencies {
    api(libs.bundles.compose.androidTest)

    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    api(project(":ui"))
    api(project(":ui-personalinfo"))

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.hilt.navigation.compose)

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(project(":testing-ui"))
}
