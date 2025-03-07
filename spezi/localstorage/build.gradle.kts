plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.spezi.localstorage"
}

dependencies {
    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(libs.hilt.test)
}
