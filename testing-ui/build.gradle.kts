plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.testing.ui"
}

dependencies {
    api(project(":ui"))
    api(libs.compose.ui.test)
    api(libs.androidx.test.runner)
}
