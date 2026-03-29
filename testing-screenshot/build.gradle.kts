plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.testing.screenshot"
}

dependencies {
    compileOnly(libs.paparazzi)
    implementation(project(":testing-concurrency"))
    implementation(project(":ui-theme"))
}
