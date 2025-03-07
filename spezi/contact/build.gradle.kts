plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.spezi.contact"
}

dependencies {
    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))
    implementation(project(":spezi:personalinfo"))
    implementation(project(":spezi:ui"))

    implementation(libs.androidx.core.i18n)
    implementation(libs.androidx.core.ktx)
}
