plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.testing"
}

dependencies {
    implementation(project(":core:coroutines"))
    implementation(project(":core:utils"))

    implementation(libs.hilt.test)
    implementation(libs.androidx.test.runner)
    implementation(libs.play.services.auth)

    api(libs.bundles.unit.testing)
    api(libs.bundles.compose.androidTest)
}
