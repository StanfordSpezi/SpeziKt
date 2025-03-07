plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.spezi.ui"
}

dependencies {
    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))

    androidTestImplementation(libs.bundles.compose.androidTest)
}
