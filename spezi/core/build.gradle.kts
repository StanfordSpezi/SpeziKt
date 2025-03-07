plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.spezi.core"
}

dependencies {
    api(libs.bundles.ktx.coroutines)

    implementation(project(":spezi:foundation"))

    implementation(libs.timber)
}
