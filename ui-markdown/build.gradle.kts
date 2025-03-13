plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.ui.markdown"
}

dependencies {
    api(libs.bundles.compose.androidTest)
    api(libs.markdown)

    api(project(":ui"))
    implementation(project(":core-logging"))
    androidTestImplementation(project(":ui-testing"))
}
