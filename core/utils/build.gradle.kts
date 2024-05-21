plugins {
    alias(libs.plugins.spezikt.library)
}

android {
    namespace = "edu.stanford.spezi.utils"
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.runtime)
}