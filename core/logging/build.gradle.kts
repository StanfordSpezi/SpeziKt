plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezikt.base)
}

android {
    namespace = "edu.stanford.spezi.logging"
}

dependencies {
    implementation(libs.timber)
    testImplementation(libs.bundles.unit.testing)
}