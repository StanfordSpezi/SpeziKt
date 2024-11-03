plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.stanford.spezi.core.design"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:utils"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose)
    implementation(libs.coil.compose)

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
