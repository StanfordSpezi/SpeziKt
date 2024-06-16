plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.storage"
}

dependencies {

    androidTestImplementation(libs.bundles.compose.androidTest)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.core.ktx)

    implementation(project(":core:utils"))

    testImplementation(libs.bundles.unit.testing)

    testImplementation(project(":core:testing"))
}
