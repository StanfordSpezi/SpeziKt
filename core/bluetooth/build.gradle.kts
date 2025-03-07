plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.core.bluetooth"
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:coroutines"))
    implementation(project(":modules:storage"))

    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))
    implementation(project(":spezi:credentialstorage"))
}
