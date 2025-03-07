plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.navigation"
}

dependencies {
    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))

    implementation(libs.androidx.core.ktx)
}
