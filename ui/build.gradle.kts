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

    api(project(":core"))
    implementation(project(":core-logging"))
    androidTestImplementation(project(":ui-testing"))
}
