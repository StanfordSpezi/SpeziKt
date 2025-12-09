plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core"
}

dependencies {
    implementation(libs.kotlin.reflect)
    api(project(":foundation"))
}
