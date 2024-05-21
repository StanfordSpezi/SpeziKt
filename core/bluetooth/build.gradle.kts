plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.bluetooth"
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:coroutines"))
}