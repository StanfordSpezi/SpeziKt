plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.foundation"
}

dependencies {
    implementation(libs.kotlin.reflect)
}
