plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.onboarding"
}

dependencies {
    implementation(project(":modules:account"))
    implementation(project(":core:coroutines"))
    implementation(project(":core:design"))
    implementation(project(":core:navigation"))
    implementation(project(":core:utils"))

    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))
    implementation(project(":spezi:ui"))

    implementation(libs.androidx.foundation)
    implementation(libs.accompanist.pager)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
}
