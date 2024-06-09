plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.utils"
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.runtime)
}
