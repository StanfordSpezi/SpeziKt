plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.stanford.spezi.core.design"

    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(project(":core:utils"))

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}