plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.storage.credential"
}

dependencies {
    api(project(":core"))
    implementation(project(":core-logging"))

    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.core.ktx)

    androidTestImplementation(libs.hilt.test)
    androidTestImplementation(project(":testing-ui"))
}
