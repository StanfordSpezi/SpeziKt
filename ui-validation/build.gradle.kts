plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.ui.validation"
}

dependencies {
    api(project(":ui"))

    androidTestImplementation(libs.bundles.compose.androidTest)
}
