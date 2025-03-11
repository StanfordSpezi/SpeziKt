plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.utils"

    defaultConfig {
        testInstrumentationRunner = "edu.stanford.spezi.ui.testing.HiltApplicationTestRunner"
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.unit.testing)
    androidTestImplementation(project(":ui-testing"))
}
