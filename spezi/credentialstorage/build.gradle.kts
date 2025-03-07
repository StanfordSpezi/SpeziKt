plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.spezi.credentialstorage"
}

dependencies {
    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.core.ktx)

    androidTestImplementation(libs.hilt.test)
}
