plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.logging"
}

dependencies {
    api(project(":core"))

    implementation(libs.timber)
}
