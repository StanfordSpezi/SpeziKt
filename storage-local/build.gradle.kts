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
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(libs.hilt.test)
    androidTestImplementation(project(":ui-testing"))
}
