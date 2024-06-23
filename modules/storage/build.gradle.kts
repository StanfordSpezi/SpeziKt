plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.storage"
}

dependencies {
    implementation(project(":core:coroutines"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.core.ktx)
}
