plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.ui.markdown"
}

dependencies {

    api(project(":ui"))
    androidTestImplementation(project(":testing-ui"))
}
