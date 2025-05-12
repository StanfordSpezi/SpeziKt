plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core"
}

dependencies {
    implementation(project(":core-logging"))
    implementation(libs.kotlin.reflect)
    api(project(":foundation"))
}
