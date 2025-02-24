plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.modules.bluetooth"
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:coroutines"))
    implementation(project(":modules:storage"))
}

