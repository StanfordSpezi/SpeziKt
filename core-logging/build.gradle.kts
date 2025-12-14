plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core.logging"
}

dependencies {
    implementation(libs.timber)
}
