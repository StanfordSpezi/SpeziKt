plugins {
    alias(libs.plugins.spezikt.library)
}

android {
    namespace = "edu.stanford.spezikt.core.design"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
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