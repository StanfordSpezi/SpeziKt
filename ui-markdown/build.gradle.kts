plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.ui.markdown"
}

dependencies {

    api(project(":ui"))
    androidTestImplementation(project(":testing-ui"))
}
