plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.modules.bluetooth"
}

dependencies {
    implementation(project(":modules:utils"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))
    implementation(project(":storage-credential"))
}
