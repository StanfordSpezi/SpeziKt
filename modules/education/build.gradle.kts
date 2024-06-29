plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.education"
}

dependencies {

    implementation(libs.androidyoutubeplayer.core)
    implementation(libs.coil.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(project(":core:navigation"))

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
}
