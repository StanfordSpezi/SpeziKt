plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.ui"
}

dependencies {

    api(project(":ui-theme"))
    implementation(project(":foundation"))
    androidTestImplementation(project(":testing-ui"))
}
