plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.ui"
}

dependencies {

    api(project(":ui-theme"))
    api(project(":core-coroutines"))

    implementation(project(":foundation"))
    androidTestImplementation(project(":testing-ui"))
}
