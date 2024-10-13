plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.modules.education"
}

dependencies {

    implementation(libs.androidyoutubeplayer.core)
    implementation(libs.coil.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(project(":core:navigation"))
}
