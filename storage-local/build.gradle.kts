plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.storage.local"
}

dependencies {
    api(project(":core"))

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(libs.hilt.test)
}
