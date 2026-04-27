plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.storage.local"
}

dependencies {
    api(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.security.crypto.ktx)
    androidTestImplementation(libs.bundles.integration.testing)
}
