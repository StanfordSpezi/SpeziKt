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
    implementation(project(":modules:design"))
    implementation(project(":modules:navigation"))
    implementation(project(":modules:utils"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":ui"))
    implementation(project(":ui-personalinfo"))
    implementation(project(":ui-markdown"))

    implementation(project(":consent"))
    implementation(project(":onboarding"))

    implementation(libs.androidx.foundation)
    implementation(libs.accompanist.pager)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(project(":testing-ui"))
}
