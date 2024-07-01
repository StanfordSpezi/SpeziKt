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
    implementation(project(":core:navigation"))
    implementation(project(":core:utils"))

    implementation(libs.androidx.foundation)
    implementation(libs.accompanist.pager)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
}
