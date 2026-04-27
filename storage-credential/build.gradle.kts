plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.storage.credential"
}

dependencies {
    api(project(":core"))

    implementation(project(":storage-local"))
    androidTestImplementation(libs.bundles.integration.testing)
}
