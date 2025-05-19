plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.stanford.spezi.modules.design"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":modules:utils"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":ui"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.compose)

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(composeBom)
    androidTestImplementation(project(":testing-ui"))

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(kotlin("reflect"))
}
