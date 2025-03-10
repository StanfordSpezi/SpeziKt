plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core"
}

dependencies {
    api(project(":foundation"))
}
