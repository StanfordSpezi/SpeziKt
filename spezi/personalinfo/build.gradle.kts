plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.spezi.personalinfo"
}

dependencies {
    implementation(project(":spezi:core"))
    implementation(project(":spezi:ui"))
}
