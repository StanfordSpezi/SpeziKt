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

    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(project(":testing-ui"))
}
