plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.ui.validation"
}

dependencies {
    api(project(":ui"))
    implementation(project(":foundation"))

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(project(":testing-ui"))
}
