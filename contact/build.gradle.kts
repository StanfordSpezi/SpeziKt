plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.contact"
}

dependencies {
    implementation(project(":core-logging"))

    api(project(":ui-personalinfo"))
    implementation(project(":ui-testing"))

    implementation(libs.androidx.core.i18n)
    implementation(libs.androidx.core.ktx)

    androidTestImplementation(libs.hilt.test)
}
