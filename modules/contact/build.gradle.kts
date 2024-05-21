plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.modules.contact"
}

dependencies {
    testImplementation(project(":core:testing"))
    androidTestImplementation(libs.bundles.compose.androidTest)
}