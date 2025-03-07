plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.navigation"
}

dependencies {
    implementation(project(":core:coroutines"))
    implementation(project(":spezi:core"))

    implementation(libs.androidx.core.ktx)
}
