plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.navigation"
}

dependencies {
    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))

    implementation(libs.androidx.core.ktx)
}
