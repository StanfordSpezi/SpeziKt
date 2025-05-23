plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.ui"
}

dependencies {
    api(libs.bundles.compose.androidTest)

    api(project(":ui-theme"))
    implementation(project(":core-logging"))
    implementation(project(":foundation"))
    androidTestImplementation(project(":testing-ui"))
}
