plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.testing"
}

dependencies {
    implementation(project(":modules:utils"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":ui"))

    implementation(libs.hilt.test)
    implementation(libs.androidx.test.runner)
    implementation(libs.play.services.auth)

    api(libs.bundles.unit.testing)
    api(libs.bundles.compose.androidTest)
}
